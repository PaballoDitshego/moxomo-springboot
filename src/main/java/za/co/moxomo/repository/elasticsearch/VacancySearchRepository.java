package za.co.moxomo.repository.elasticsearch;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import za.co.moxomo.domain.Vacancy;

import java.util.Date;
import java.util.List;

public interface VacancySearchRepository extends ElasticsearchRepository<Vacancy, String> {

    List<Vacancy> findByOfferIdAndAndCompany(String offerId, String company);
    List<Vacancy> findByAdvertDateLessThan(Date date);
}
