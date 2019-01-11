package za.co.moxomo.services;

import za.co.moxomo.model.Vacancy;
import za.co.moxomo.model.wrapper.SearchResponse;

/**
 * Created by paballo on 2017/02/20.
 */

public interface SearchService {

    Vacancy indexDocument(Vacancy vacancy);

    Vacancy getVacancy(String id);

    SearchResponse search(String searchString, int offset, int limit);
}
