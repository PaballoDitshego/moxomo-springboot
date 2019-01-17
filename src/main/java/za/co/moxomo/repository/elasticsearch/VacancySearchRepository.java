package za.co.moxomo.repository.elasticsearch;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import za.co.moxomo.model.Vacancy;

public interface VacancySearchRepository  extends ElasticsearchRepository<Vacancy, String> {
    Vacancy findByOfferIdAndAndCompany(String offerId, String company);
}
