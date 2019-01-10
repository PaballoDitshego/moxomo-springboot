package za.co.moxomo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;
import za.co.moxomo.model.wrapper.ResponseWrapper;
import za.co.moxomo.services.SearchService;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class VacancyRestController {

    private TaskExecutor taskExecutor;
    private SearchService searchService;

    @Autowired
    public VacancyRestController(TaskExecutor taskExecutor, SearchService searchService) {
        this.taskExecutor = taskExecutor;
        this.searchService = searchService;
    }

    @GetMapping(value = "/vacancies")
    @CrossOrigin
    public DeferredResult<ResponseEntity<ResponseWrapper>> getVacancies(@RequestParam int offset, @RequestParam int limit) throws Exception {

        DeferredResult<ResponseEntity<ResponseWrapper>> deferredResult = new DeferredResult<>();
        CompletableFuture.supplyAsync(() -> {
            ResponseWrapper erResponse;
            try {
                erResponse = searchService.getAllVacancies(offset, limit);
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
            return erResponse;
        }, taskExecutor).whenCompleteAsync((result, error) -> {
            if (Objects.nonNull(error)) {
                deferredResult.setErrorResult(error);
            } else {
                deferredResult.setResult(new ResponseEntity<>(result, HttpStatus.OK));
            }
        }, taskExecutor);
        return deferredResult;

    }
}
