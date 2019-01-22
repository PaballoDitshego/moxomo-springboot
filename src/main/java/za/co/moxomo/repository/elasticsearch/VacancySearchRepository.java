package za.co.moxomo.repository.elasticsearch;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import za.co.moxomo.model.Vacancy;

import java.util.List;

public interface VacancySearchRepository  extends ElasticsearchRepository<Vacancy, String> {
    List<Vacancy> findByOfferIdAndAndCompany(String offerId, String company);
}
