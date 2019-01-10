package repository;

import co.moxomo.model.Vacancy;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface VacancySearchRepository  extends ElasticsearchRepository<Vacancy, String> {

}
