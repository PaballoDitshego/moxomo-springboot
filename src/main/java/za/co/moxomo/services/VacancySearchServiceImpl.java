package za.co.moxomo.services;

import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.percolator.PercolateQueryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import za.co.moxomo.model.wrapper.SearchResponse;
import za.co.moxomo.repository.VacancySearchRepository;
import za.co.moxomo.utils.Util;

import java.util.Objects;

import static org.elasticsearch.index.query.Operator.AND;
import static org.elasticsearch.index.query.QueryBuilders.matchAllQuery;
import static org.elasticsearch.index.query.QueryBuilders.multiMatchQuery;


@Service
public class SearchServiceImpl implements SearchService {

    private static final Logger logger = LoggerFactory.getLogger(SearchServiceImpl.class);

    @Override
    public boolean isExists(Vacancy vacancy) {
        return vacancySearchRepository.findByOfferIdAndAndCompany(vacancy.getOfferId(),vacancy.getCompany()).size()>0;
    }

    private VacancySearchRepository vacancySearchRepository;
    private ElasticsearchOperations elasticsearchTemplate;

    @Autowired
    public SearchServiceImpl(VacancySearchRepository vacancySearchRepository, ElasticsearchOperations elasticsearchTemplate) {
        this.vacancySearchRepository = vacancySearchRepository;
        this.elasticsearchTemplate = elasticsearchTemplate;
    }

    @Override
    public Vacancy index(Vacancy vacancy) {
        if(!Util.validate(vacancy)){
            throw new IllegalArgumentException("Vacancy missing some compulsory parameters");
        }
        vacancySearchRepository.save(vacancy);
        return vacancy;
    }

    @Override
    public Vacancy getVacancy(final String id) {
        Objects.requireNonNull(id);
        return vacancySearchRepository.findById(id).get();
    }

    @Override
    public SearchResponse search(String searchString, int offset, int limit) {
        logger.info("Running query {}", searchString);
        final PageRequest pageRequest = PageRequest.of(offset - 1, limit, Sort.Direction.DESC, "advertDate");
        final SourceFilter sourceFilter = new FetchSourceFilter(new String[]{"id", "jobTitle", "description", "location",
                "advertDate", "imageUrl", "url", "webViewViewable"}, null);
        final SearchQuery searchQuery = new NativeSearchQueryBuilder().withIndices("jobs")
                .withQuery((Objects.isNull(searchString) || searchString.equals("")) ? matchAllQuery() :
                        multiMatchQuery(searchString)
                                .field("jobTitle", 60)
                                .field("description")
                                .field("additionalTokens")
                                .field("responsibilities")
                                .field("location")
                                .field("company", 5)
                                .field("qualifications")
                                .field("contractType")
                                .operator(AND)
                                .fuzziness(Fuzziness.AUTO)
                                .prefixLength(4)
                                .type(MultiMatchQueryBuilder.Type.PHRASE))
                .withSourceFilter(sourceFilter)
                .withPageable(pageRequest)
                .build();

        PercolateQueryBuilder percolateQuery = null;

        final int totalNumberOfElements = (int) (elasticsearchTemplate.count(searchQuery));
        logger.debug("Found {} matching items for searchString {}", totalNumberOfElements, searchQuery);
        int totalNumberOfPages = 1;
        if (totalNumberOfElements > 0) {
            totalNumberOfPages = (totalNumberOfElements / limit) == 0 ? 1 : (totalNumberOfElements / limit);
        }
        final Page<Vacancy> vacancies = elasticsearchTemplate.queryForPage(searchQuery, Vacancy.class);

        return new SearchResponse(offset, totalNumberOfPages, vacancies.getContent());
    }


}