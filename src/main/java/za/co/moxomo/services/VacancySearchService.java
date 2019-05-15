package za.co.moxomo.services;

import za.co.moxomo.domain.AlertPreference;
import za.co.moxomo.domain.Vacancy;
import za.co.moxomo.dto.wrapper.SearchResults;

import java.io.IOException;
import java.util.List;


public interface VacancySearchService {

    Vacancy index(Vacancy vacancy) throws Exception;

    Vacancy getByCompanyAndOfferId(Vacancy vacancy);

    Vacancy getVacancy(String id);

    boolean isExists(Vacancy vacancy);

    SearchResults search(String searchString, int offset, int limit);

    void deleteOldVacancies();

    void delete(Vacancy vacancy);

    AlertPreference createSearchPreference(AlertPreference alertPreference) throws IOException;

    List<String > getSearchSuggestions(String term);
}
