package za.co.moxomo.services;

import za.co.moxomo.model.Vacancy;
import za.co.moxomo.model.wrapper.SearchResults;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;


public interface VacancySearchService {

    Vacancy index(Vacancy vacancy) throws Exception;

    Vacancy getVacancy(String id);

    boolean isExists(Vacancy vacancy);

    SearchResults search(String searchString, int offset, int limit);

    void deleteOldVacancies();
}
