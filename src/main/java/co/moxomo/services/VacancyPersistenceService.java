package co.moxomo.services;

import co.moxomo.model.Vacancy;

/**
 * Created by paballo on 2017/02/20.
 */
public interface VacancyPersistenceService {

    void persistVacancy(Vacancy vacancy);

    void deleteVacancy(String vacancyId);

    void deleteVacancies(String criteriaValue);


}
