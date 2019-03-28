package za.co.moxomo.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.bytes.BytesReference;
import org.elasticsearch.common.geo.GeoPoint;
import org.elasticsearch.common.lucene.search.function.CombineFunction;
import org.elasticsearch.common.lucene.search.function.FunctionScoreQuery;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.functionscore.FunctionScoreQueryBuilder;
import org.elasticsearch.index.query.functionscore.GaussDecayFunctionBuilder;
import org.elasticsearch.index.query.functionscore.ScoreFunctionBuilders;
import org.elasticsearch.percolator.PercolateQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.data.elasticsearch.core.query.SourceFilter;
import org.springframework.stereotype.Service;
import za.co.moxomo.model.Vacancy;
import za.co.moxomo.model.wrapper.SearchResults;
import za.co.moxomo.repository.elasticsearch.VacancySearchRepository;
import za.co.moxomo.utils.Util;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static org.elasticsearch.index.query.Operator.AND;
import static org.elasticsearch.index.query.QueryBuilders.*;


@Service
public class VacancySearchServiceImpl implements VacancySearchService {

    private static final Logger logger = LoggerFactory.getLogger(VacancySearchServiceImpl.class);
    public static final String JOBS = "jobs";
    private ObjectMapper objectMapper = new ObjectMapper();


    private VacancySearchRepository vacancySearchRepository;
    private ElasticsearchOperations elasticsearchTemplate;

    @Autowired
    public VacancySearchServiceImpl(VacancySearchRepository vacancySearchRepository, ElasticsearchOperations elasticsearchTemplate) {
        this.vacancySearchRepository = vacancySearchRepository;
        this.elasticsearchTemplate = elasticsearchTemplate;
    }

    @Override
    public Vacancy index(Vacancy vacancy) throws Exception {
        if (!Util.validate(vacancy)) {
            throw new IllegalArgumentException("Vacancy missing some compulsory parameters");
        }
        try {
            vacancy = vacancySearchRepository.save(vacancy);
            logger.info("Save vacancy {}", objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(vacancy));
            performPercolationQuery(vacancy);
        } catch (Exception e) {
            Marker timeMarker = MarkerFactory.getMarker("time");
            logger.error(timeMarker, objectMapper.writeValueAsString(vacancy), e);
        }

        return vacancy;
    }

    @Override
    public boolean isExists(Vacancy vacancy) {
        return vacancySearchRepository.findByOfferIdAndAndCompany(vacancy.getOfferId(), vacancy.getCompany()).size() > 0;
    }


    @Override
    public Vacancy getVacancy(final String id) {
        Objects.requireNonNull(id);
        return vacancySearchRepository.findById(id).get();
    }

    @Override
    public SearchResults search(String searchString, int offset, int limit) {
        logger.info("Running query {}", searchString);

        MultiMatchQueryBuilder multiMatchQuery = null;
        if (Objects.nonNull(searchString)) {
            multiMatchQuery = QueryBuilders.multiMatchQuery(
                    searchString, "jobTitle^0.8", "description", "additionalTokens", "responsibilities", "location", "company^0.9", "qualifications")
                    .type(MultiMatchQueryBuilder.Type.PHRASE).lenient(true).autoGenerateSynonymsPhraseQuery(true);
        }
        final GaussDecayFunctionBuilder gaussDecayFunctionBuilder = ScoreFunctionBuilders.gaussDecayFunction("advertDate", "now", "5h", "5" +
                "h", 0.75);
        final FunctionScoreQueryBuilder query = QueryBuilders.functionScoreQuery((Objects.nonNull(searchString)) ? multiMatchQuery.minimumShouldMatch("2") : matchAllQuery(), gaussDecayFunctionBuilder);
        query.boostMode(CombineFunction.MULTIPLY);

        final PageRequest pageRequest = PageRequest.of(offset - 1, limit);
        final SourceFilter sourceFilter = new FetchSourceFilter(new String[]{"id", "jobTitle", "description", "location",
                "advertDate", "imageUrl", "url", "webViewViewable"}, null);
        final SearchQuery searchQuery = new NativeSearchQueryBuilder().withIndices(JOBS)
                .withQuery(query)
                .withSourceFilter(sourceFilter)
                .withPageable(Objects.isNull(searchString) ?
                        PageRequest.of(offset - 1, limit, Sort.Direction.DESC, "advertDate") : pageRequest)
                .build();
        final int totalNumberOfElements = (int) (elasticsearchTemplate.count(searchQuery));
        logger.info("Found {} matching items for searchString {}", totalNumberOfElements, searchQuery);
        int totalNumberOfPages = 1;
        if (totalNumberOfElements > 0) {
            totalNumberOfPages = (totalNumberOfElements / limit) == 0 ? 1 : (totalNumberOfElements / limit);
        }
        final Page<Vacancy> vacancies = vacancySearchRepository.search(searchQuery);

        return new SearchResults(offset,vacancies.getTotalElements(), totalNumberOfPages, vacancies.getContent());
    }

    @Override
    public void deleteOldVacancies() {
        logger.info("Deleting vacancies older that 30 days. The number of vacancies before deletion is {}", vacancySearchRepository.count());
        LocalDateTime localDate = LocalDateTime.now().minus(Duration.ofDays(31)).atZone(ZoneId.of("Africa/Johannesburg")).toLocalDateTime();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'hh:mm:ss");
        String date = localDate.format(dateTimeFormatter);

        QueryBuilder queryBuilder = QueryBuilders.boolQuery()
                .must(QueryBuilders.rangeQuery("advertDate")
                        .lte(date));

        Iterable<Vacancy> vacanciesToDelete = vacancySearchRepository.search(queryBuilder);
        vacanciesToDelete.forEach(vacancySearchRepository::delete);
        logger.info("The number of vacancies after deletion is {}", vacancySearchRepository.count());


    }

    private void performPercolationQuery(Vacancy vacancy) {

        GetResponse response = elasticsearchTemplate.getClient().prepareGet(JOBS, "_doc", vacancy.getId()).get();
        BytesReference bytesReference = response.getSourceAsBytesRef();
        PercolateQueryBuilder percolateQuery = new PercolateQueryBuilder("query", bytesReference, XContentType.JSON);
        SearchResponse searchResponse = elasticsearchTemplate.getClient().prepareSearch("myIndexName")
                .setQuery(percolateQuery).get();
        for (SearchHit hit : searchResponse.getHits()) {

            // Percolator queries as hit
        }


    }

}