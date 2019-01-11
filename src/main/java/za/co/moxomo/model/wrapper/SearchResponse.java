package za.co.moxomo.model.wrapper;

import za.co.moxomo.model.Vacancy;

import java.util.List;

public class SearchResponse extends  ResponseWrapper{

    private int page;
    private long totalPages;
    List<Vacancy> vacancies;


    public SearchResponse(int page, long totalPages, List<Vacancy> vacancies) {
        this.page = page;
        this.totalPages = totalPages;
        this.vacancies = vacancies;
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
