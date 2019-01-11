package za.co.moxomo.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;
import za.co.moxomo.model.Vacancy;
import za.co.moxomo.model.wrapper.ResponseWrapper;
import za.co.moxomo.services.SearchService;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

@RestController
public class SearchServiceRestController {

    private static final Logger logger = LoggerFactory.getLogger(SearchServiceRestController.class);

    private TaskExecutor asyncExecutor;
    private SearchService searchService;

    @Autowired
    public SearchServiceRestController(TaskExecutor asyncExecutor, SearchService searchService) {
        this.asyncExecutor = asyncExecutor;
        this.searchService = searchService;
    }

    @GetMapping(value = "/vacancies")
    @CrossOrigin
    public DeferredResult<ResponseEntity<ResponseWrapper>> getVacancies(@RequestParam(required = false) String searchString, @RequestParam int offset, @RequestParam int limit) throws Exception {

        DeferredResult<ResponseEntity<ResponseWrapper>> deferredResult = new DeferredResult<>();
        CompletableFuture.supplyAsync(() -> {
            ResponseWrapper response;
            try {
                response = searchService.search(searchString,offset, limit);
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
            return response;
        }, asyncExecutor).whenCompleteAsync((result, error) -> {
            if (Objects.nonNull(error)) {
                deferredResult.setErrorResult(error);
            } else {
                deferredResult.setResult(new ResponseEntity<>(result, HttpStatus.OK));
            }
        }, asyncExecutor);
        return deferredResult;

    }

    @GetMapping(value = "/vacancies/{id}")
    @CrossOrigin
    public DeferredResult<ResponseEntity<Vacancy>> getVacancy(@PathVariable("id") String id) throws Exception {

        DeferredResult<ResponseEntity<Vacancy>> deferredResult = new DeferredResult<>();
        CompletableFuture.supplyAsync(() -> {
            Vacancy response;
            try {
                response = searchService.getVacancy(id);
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
            return response;
        }, asyncExecutor).whenCompleteAsync((result, error) -> {
            if (Objects.nonNull(error)) {
                deferredResult.setErrorResult(error);
            } else {
                deferredResult.setResult(new ResponseEntity<>(result, HttpStatus.OK));
            }
        }, asyncExecutor);
        return deferredResult;

    }
}
