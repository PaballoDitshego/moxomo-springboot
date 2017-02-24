package co.moxomo.services;

import co.moxomo.model.Vacancy;

import java.util.List;

/**
 * Created by paballo on 2017/02/20.
 */
public interface VacancyService {

    void createOrUpdateVacancy(Vacancy vacancy);

    void deleteVacancy(String vacancyId);

    void deleteVacancies(String criteriaValue);

    boolean documentExists(String website);
}
