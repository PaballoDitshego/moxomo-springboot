package za.co.moxomo.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.WriteRequest;
import org.elasticsearch.common.bytes.BytesReference;
import org.elasticsearch.common.geo.GeoPoint;
import org.elasticsearch.common.lucene.search.function.CombineFunction;
import org.elasticsearch.common.unit.DistanceUnit;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.*;
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
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.data.elasticsearch.core.query.SourceFilter;
import org.springframework.stereotype.Service;
import za.co.moxomo.domain.AlertPreference;
import za.co.moxomo.domain.Notification;
import za.co.moxomo.domain.Vacancy;
import za.co.moxomo.enums.PercolatorIndexFields;
import za.co.moxomo.dto.wrapper.SearchResults;
import za.co.moxomo.repository.mongodb.AlertPreferenceRepository;
import za.co.moxomo.repository.elasticsearch.VacancySearchRepository;
import za.co.moxomo.repository.mongodb.SearchSuggestionKeywordRepository;
import za.co.moxomo.utils.Util;

import java.io.IOException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;
import static org.elasticsearch.index.query.QueryBuilders.*;
import static za.co.moxomo.config.Config.PERCOLATOR_INDEX;
import static za.co.moxomo.config.Config.PERCOLATOR_INDEX_MAPPING_TYPE;


@Service
public class VacancySearchServiceImpl implements VacancySearchService {

    private static final Logger logger = LoggerFactory.getLogger(VacancySearchServiceImpl.class);
    private static final String JOBS = "job_ads";
    private ObjectMapper objectMapper = new ObjectMapper();


    private VacancySearchRepository vacancySearchRepository;

    private ElasticsearchTemplate elasticsearchTemplate;
    private AlertPreferenceRepository alertPreferenceRepository;
    private NotificationSendingService notificationSendingService;
    private SearchSuggestionKeywordRepository searchSuggestionKeywordRepository;
    private GeoService geoService;

    @Autowired
    public VacancySearchServiceImpl(VacancySearchRepository vacancySearchRepository, AlertPreferenceRepository alertPreferenceRepository,ElasticsearchTemplate elasticSearch,
                                    NotificationSendingService notificationSendingService, SearchSuggestionKeywordRepository searchSuggestionKeywordRepository, GeoService geoService) {
        this.vacancySearchRepository = vacancySearchRepository;
        this.elasticsearchTemplate = elasticSearch;
        this.alertPreferenceRepository = alertPreferenceRepository;
        this.notificationSendingService = notificationSendingService;
        this.searchSuggestionKeywordRepository = searchSuggestionKeywordRepository;
        this.geoService = geoService;
    }

    @Override
    public Vacancy index(Vacancy vacancy) throws Exception {
        if (!Util.validate(vacancy)) {
            throw new IllegalArgumentException("Vacancy missing some compulsory parameters");
        }
        try {
            vacancy = geoService.geoCode(vacancy);
            final Vacancy savedVacancy = vacancySearchRepository.save(vacancy);
            logger.debug("Save vacancy {}", objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(savedVacancy));
            if(Objects.nonNull(vacancy)) {
                List<AlertPreference> alertPreferences = findMatchingPreferences(vacancy);
                alertPreferences.forEach(alertPreference -> {
                    Notification notification = Util.generateNotification(savedVacancy, alertPreference);
                    notificationSendingService.sendAlert(notification);
                });
            }

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
    public Vacancy getByCompanyAndOfferId(Vacancy vacancy) {
        return vacancySearchRepository.findByOfferIdAndAndCompany(vacancy.getOfferId(), vacancy.getCompany()).get(0);
    }


    @Override
    public Vacancy getVacancy(final String id) {
        Objects.requireNonNull(id);
        return vacancySearchRepository.findById(id).get();
    }

    @Override
    public SearchResults search(String searchString, double latitude, double longitude, int offset, int limit) {
        logger.info("Running query {}, latitude {}, longitude {}", searchString, latitude, longitude);


        MultiMatchQueryBuilder multiMatchQuery = null;
        if (Objects.nonNull(searchString)) {
            multiMatchQuery = QueryBuilders.multiMatchQuery(
                    searchString).field("jobTitle", 5)
                    .field("company", 5)
                    // .field("description")
                    .field("location", 8)
                    //.minimumShouldMatch("2")
                    .fuzziness(Fuzziness.AUTO)
                    .fuzzyTranspositions(false)

                    .type(MultiMatchQueryBuilder.Type.MOST_FIELDS);
        }
        GeoPoint geoPoint = null;


        final GaussDecayFunctionBuilder advertDateDecayFunctionBuilder = ScoreFunctionBuilders.gaussDecayFunction("advertDate", "now", "8h", "4h", 0.5).setWeight(5);

        FunctionScoreQueryBuilder.FilterFunctionBuilder[] functions = new FunctionScoreQueryBuilder.FilterFunctionBuilder[2];

        if (latitude != 0.0 && longitude != 0.0) {
            geoPoint = new GeoPoint(latitude, longitude);
        }
        if (Objects.nonNull(geoPoint)) {
            GaussDecayFunctionBuilder geoBuilder = ScoreFunctionBuilders.gaussDecayFunction("geoPoint", geoPoint, "20km", "35km", 0.75).setWeight(4);
            functions[1] = new FunctionScoreQueryBuilder.FilterFunctionBuilder(advertDateDecayFunctionBuilder);
            functions[0] = new FunctionScoreQueryBuilder.FilterFunctionBuilder(geoBuilder);

        }
        final FunctionScoreQueryBuilder query;
        if (Objects.isNull(geoPoint)) {
            query = QueryBuilders.functionScoreQuery((Objects.nonNull(searchString)) ? multiMatchQuery.minimumShouldMatch("2") : matchAllQuery(), advertDateDecayFunctionBuilder);
        } else {
            query = QueryBuilders.functionScoreQuery((Objects.nonNull(searchString)) ? multiMatchQuery.minimumShouldMatch("2") : matchAllQuery(), functions);
        }

        query.boostMode(CombineFunction.MULTIPLY);
        final PageRequest pageRequest = PageRequest.of(offset - 1, limit);
        final SourceFilter sourceFilter = new FetchSourceFilter(new String[]{"id", "jobTitle", "description", "location",
                "advertDate", "imageUrl", "url", "webViewViewable", "company", "geoPoint"}, null);
        final SearchQuery searchQuery = new NativeSearchQueryBuilder().withIndices(JOBS)
                .withQuery(query)
                .withSourceFilter(sourceFilter)
                .withPageable(Objects.isNull(searchString) ?
                            PageRequest.of(offset - 1, limit, Sort.Direction.DESC, "advertDate") : pageRequest)
               // .withPageable(pageRequest)
                .build();

        final int totalNumberOfElements = (int) (elasticsearchTemplate.count(searchQuery));
        logger.debug("Found {} matching items for searchString {}", totalNumberOfElements, searchQuery);
        int totalNumberOfPages = 1;
        if (totalNumberOfElements > 0) {
            totalNumberOfPages = (totalNumberOfElements / limit) == 0 ? 1 : (totalNumberOfElements / limit);
        }
        final Page<Vacancy> vacancies = vacancySearchRepository.search(searchQuery);
        if (Objects.nonNull(geoPoint)) {
            return new SearchResults(offset, vacancies.getTotalElements(), totalNumberOfPages, getVacanciesWithDistance(vacancies.getContent(), geoPoint));
        }

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
    public void delete(Vacancy vacancy) {
        vacancySearchRepository.delete(vacancy);
    }

    @Override
    public AlertPreference createSearchPreference(AlertPreference alertPreference) throws IOException {
        Objects.requireNonNull(alertPreference);
        alertPreference = alertPreferenceRepository.save(alertPreference);
        BoolQueryBuilder bqb = createBoolQuery(alertPreference);

        elasticsearchTemplate.getClient().prepareIndex(PERCOLATOR_INDEX, PERCOLATOR_INDEX_MAPPING_TYPE, alertPreference.getId())
                .setSource(jsonBuilder()
                        .startObject()
                        .field(PercolatorIndexFields.PERCOLATOR_QUERY.getFieldName(), bqb) // Register the query
                        .endObject())
                .setRefreshPolicy(WriteRequest.RefreshPolicy.IMMEDIATE) // Needed when the query shall be available immediately
                .get();

        return alertPreference;
    }

    @Override
    public List<String> getSearchSuggestions(String term) {
        return searchSuggestionKeywordRepository.findAllByKeywordStartsWithIgnoreCase(term)
                .stream().map(s -> s.getKeyword()).collect(Collectors.toList());
    }

    private List<AlertPreference> findMatchingPreferences(Vacancy vacancy) throws IOException {
        logger.info("Finding matching preferences");
        List<AlertPreference> results = new ArrayList<>();
        PercolateQueryBuilder percolateQuery = createPercolateQuery(vacancy);
        // Percolate, by executing the percolator query in the query dsl
        SearchResponse searchResponse = elasticsearchTemplate.getClient().prepareSearch(PERCOLATOR_INDEX)
                .setQuery(percolateQuery)
                .execute()
                .actionGet();
        if (searchResponse != null) {
            SearchHits searchHits = searchResponse.getHits();
            logger.info("Number of searchHits {}", searchHits.totalHits);
            if (searchHits != null && searchHits.getTotalHits() > 0) {
                for (SearchHit hit : searchHits.getHits()) {
                    AlertPreference alertPreference;
                    if (Objects.nonNull(alertPreference = alertPreferenceRepository.findAlertPreferenceById(hit.getId()))) {
                        results.add(alertPreference);
                    }
                }
            }
        }
        return results;
    }


    private BoolQueryBuilder
    createBoolQuery(AlertPreference preference) {


        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.should(QueryBuilders.matchQuery(PercolatorIndexFields.KEYWORD.getFieldName(), preference.getCriteria().getKeyword()).operator(Operator.AND).lenient(true));
        boolQueryBuilder.should(QueryBuilders.matchQuery(PercolatorIndexFields.COMPANY.getFieldName(), preference.getCriteria().getKeyword()).lenient(true));

        if (preference.getCriteria().getPoint() != null) {
            double[] point = preference.getCriteria().getPoint();
            boolQueryBuilder.filter(QueryBuilders.geoDistanceQuery(PercolatorIndexFields.GEOPOINT.getFieldName()).point(new GeoPoint(point[0], point[1])).distance(35, DistanceUnit.KILOMETERS));
        }
        boolQueryBuilder.minimumShouldMatch(1);

        return boolQueryBuilder;
    }


    private PercolateQueryBuilder createPercolateQuery(Vacancy vacancy) throws IOException {
        XContentBuilder docBuilder = XContentFactory.jsonBuilder().startObject();
        docBuilder.field(PercolatorIndexFields.KEYWORD.getFieldName(), vacancy.getJobTitle());
        docBuilder.field(PercolatorIndexFields.COMPANY.getFieldName(), vacancy.getCompany());
        //  docBuilder.field(PercolatorIndexFields.LOCATION.getFieldName(), vacancy.getLocation());
        docBuilder.startObject(PercolatorIndexFields.GEOPOINT.getFieldName())
                .field("lat", vacancy.getGeoPoint().getLat()).field("lon", vacancy.getGeoPoint().getLon());
        docBuilder.endObject();
        docBuilder.endObject();
        logger.debug("Percolator query {}", BytesReference.bytes(docBuilder).utf8ToString());

        return new PercolateQueryBuilder(PercolatorIndexFields.PERCOLATOR_QUERY.getFieldName(),
                BytesReference.bytes(docBuilder),
                XContentType.JSON);
    }

    private List<Vacancy> getVacanciesWithDistance(List<Vacancy> vacancies, GeoPoint geoPoint) {
        return vacancies.stream()
                .filter(s -> Objects.nonNull(s.getGeoPoint()))
                .map(s -> {
                    if (s.getGeoPoint() == null) {
                        logger.info("s without distance {}", s.toString());
                    }
                    double distance = Util.distance(s.getGeoPoint().getLat(), s.getGeoPoint().getLon(), geoPoint.getLat(), geoPoint.getLon(), "K");
                    s.setDistance(Double.toString(Math.floor(distance)).concat(" KM"));
                    s.setGeoPoint(null);
                    return s;

                }).collect(Collectors.toList());
    }


}
