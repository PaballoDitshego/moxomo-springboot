package za.co.moxomo.model.wrapper;

import za.co.moxomo.model.Vacancy;

import java.util.List;

public class SearchResults extends  ResponseWrapper{

    private int page;
    private long totalPages;
    private long numberOfResults;
    List<Vacancy> vacancies;


    public SearchResults(int page, long numberOfResults, long totalPages, List<Vacancy> vacancies) {
        this.page = page;
        this.totalPages = totalPages;
        this.vacancies = vacancies;
        this.numberOfResults=numberOfResults;
    }

    public long getNumberOfResults() {
        return numberOfResults;
    }

    public int getPage() {
        return page;
    }


    public long getTotalPages() {
        return totalPages;
    }

    public List<Vacancy> getVacancies() {
        return vacancies;
    }
}
