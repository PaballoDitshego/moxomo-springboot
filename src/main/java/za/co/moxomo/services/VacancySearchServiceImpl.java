package za.co.moxomo.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.WriteRequest;
import org.elasticsearch.common.bytes.BytesReference;
import org.elasticsearch.common.lucene.search.function.CombineFunction;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.functionscore.FunctionScoreQueryBuilder;
import org.elasticsearch.index.query.functionscore.GaussDecayFunctionBuilder;
import org.elasticsearch.index.query.functionscore.ScoreFunctionBuilders;
import org.elasticsearch.percolator.PercolateQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
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
import za.co.moxomo.domain.AlertPreference;
import za.co.moxomo.domain.Vacancy;
import za.co.moxomo.enums.PercolatorIndexFields;
import za.co.moxomo.dto.wrapper.SearchResults;
import za.co.moxomo.repository.mongodb.AlertPreferenceRepository;
import za.co.moxomo.repository.elasticsearch.VacancySearchRepository;
import za.co.moxomo.utils.Util;

import java.io.IOException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;
import static org.elasticsearch.index.query.QueryBuilders.*;
import static za.co.moxomo.config.Config.PERCOLATOR_INDEX;
import static za.co.moxomo.config.Config.PERCOLATOR_INDEX_MAPPING_TYPE;


@Service
public class VacancySearchServiceImpl implements VacancySearchService {

    private static final Logger logger = LoggerFactory.getLogger(VacancySearchServiceImpl.class);
    private static final String JOBS = "jobs";
    private ObjectMapper objectMapper = new ObjectMapper();


    private VacancySearchRepository vacancySearchRepository;
    private ElasticsearchOperations elasticsearchTemplate;
    private AlertPreferenceRepository alertPreferenceRepository;

    @Autowired
    public VacancySearchServiceImpl(VacancySearchRepository vacancySearchRepository, AlertPreferenceRepository alertPreferenceRepository, ElasticsearchOperations elasticsearchTemplate) {
        this.vacancySearchRepository = vacancySearchRepository;
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.alertPreferenceRepository = alertPreferenceRepository;
    }

    @Override
    public Vacancy index(Vacancy vacancy) throws Exception {
        if (!Util.validate(vacancy)) {
            throw new IllegalArgumentException("Vacancy missing some compulsory parameters");
        }
        try {
            vacancy = vacancySearchRepository.save(vacancy);
            List<AlertPreference> alertPreferences = findMatchingPreferences(vacancy);
            logger.info("Save vacancy {}", objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(vacancy));

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
                    searchString).field("jobTitle", 5)
                    .field("company", 2)
                    .field("description")
                    .field("location", 2)
                    //     .field("additionalTokens")
                    .type(MultiMatchQueryBuilder.Type.PHRASE);
        }
        final GaussDecayFunctionBuilder gaussDecayFunctionBuilder = ScoreFunctionBuilders.gaussDecayFunction("advertDate", "now", "3h", "2h", 0.5);
        final FunctionScoreQueryBuilder query = QueryBuilders.functionScoreQuery((Objects.nonNull(searchString)) ? multiMatchQuery.minimumShouldMatch("2") : matchAllQuery(), gaussDecayFunctionBuilder);
        query.boostMode(CombineFunction.MULTIPLY);

        final PageRequest pageRequest = PageRequest.of(offset - 1, limit);
        final SourceFilter sourceFilter = new FetchSourceFilter(new String[]{"id", "jobTitle", "description", "location",
                "advertDate", "imageUrl", "url", "webViewViewable", "company"}, null);
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

        return new SearchResults(offset, vacancies.getTotalElements(), totalNumberOfPages, vacancies.getContent());
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

    @Override
    public AlertPreference createSearchPreference(AlertPreference alertPreference) throws IOException {
        Objects.requireNonNull(alertPreference);
       alertPreferenceRepository.save(alertPreference);
        BoolQueryBuilder bqb = createBoolQuery(alertPreference);
        elasticsearchTemplate.getClient().prepareIndex(PERCOLATOR_INDEX, PERCOLATOR_INDEX_MAPPING_TYPE, alertPreference.getAlertPreferenceId())
                .setSource(jsonBuilder()
                        .startObject()
                        .field(PercolatorIndexFields.PERCOLATOR_QUERY.getFieldName(), bqb) // Register the query
                        .endObject())
                .setRefreshPolicy(WriteRequest.RefreshPolicy.IMMEDIATE) // Needed when the query shall be available immediately
                .get();

        return alertPreference;
    }

    private List<AlertPreference> findMatchingPreferences(Vacancy vacancy) throws IOException {
        List<AlertPreference> results = new ArrayList<>();
        PercolateQueryBuilder percolateQuery = createPercolateQuery(vacancy);

        // Percolate, by executing the percolator query in the query dsl:
        SearchResponse searchResponse = elasticsearchTemplate.getClient().prepareSearch(PERCOLATOR_INDEX)
                .setQuery(percolateQuery)
                .execute()
                .actionGet();

        if (searchResponse != null) {
            SearchHits searchHits = searchResponse.getHits();
            if (searchHits != null && searchHits.getTotalHits() > 0) {
                for (SearchHit hit : searchHits.getHits()) {
                    results.add(alertPreferenceRepository.findById(hit.getId()).get());
                }
            }
        }

        return results;
    }


    private BoolQueryBuilder createBoolQuery(AlertPreference preference) {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        if (preference.getCriteria().getJobTitle() != null) {
            boolQueryBuilder.must(QueryBuilders.termsQuery(PercolatorIndexFields.JOB_TITLE.getFieldName(), preference.getCriteria().getJobTitle()));
        }

        if (preference.getCriteria().getProvince() != null && preference.getCriteria().getProvince().length > 0) {
            Arrays.stream(preference.getCriteria().getProvince()).forEach(s -> {
                        boolQueryBuilder.should(QueryBuilders.termsQuery(PercolatorIndexFields.PROVINCE.getFieldName(), s)).minimumShouldMatch(1);

                    }
            );
        }
        if (preference.getCriteria().getTown() != null && preference.getCriteria().getTown().length > 0) {
            Arrays.stream(preference.getCriteria().getProvince()).forEach(s -> {
                        boolQueryBuilder.should(QueryBuilders.termsQuery(PercolatorIndexFields.TOWN.getFieldName(), s)).minimumShouldMatch(1);

                    }
            );
        }
        if (preference.getCriteria().getTags() != null && preference.getCriteria().getTags().length > 0) {
            Arrays.stream(preference.getCriteria().getProvince()).forEach(s -> {
                        boolQueryBuilder.should(QueryBuilders.termsQuery(PercolatorIndexFields.TAGS.getFieldName(), s)).minimumShouldMatch(1);

                    }
            );
        }

        return boolQueryBuilder;
    }


    private PercolateQueryBuilder createPercolateQuery(Vacancy vacancy) throws IOException {
        //Build a document to check against the percolator
        XContentBuilder docBuilder = XContentFactory.jsonBuilder().startObject();
        docBuilder.field(PercolatorIndexFields.JOB_TITLE.getFieldName(), vacancy.getJobTitle());
        docBuilder.array(PercolatorIndexFields.TOWN.getFieldName(), vacancy.getLocation());
        docBuilder.array(PercolatorIndexFields.PROVINCE.getFieldName(), vacancy.getLocation());
        // docBuilder.field(PercolatorIndexFields.TYPE.getFieldName(), book.getType());*/
        docBuilder.endObject();

        return new PercolateQueryBuilder(PercolatorIndexFields.PERCOLATOR_QUERY.getFieldName(),
                BytesReference.bytes(docBuilder),
                XContentType.JSON);
    }


}
