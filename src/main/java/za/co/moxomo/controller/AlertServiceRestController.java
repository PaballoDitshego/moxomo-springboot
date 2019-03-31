package za.co.moxomo.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import za.co.moxomo.domain.AlertPreference;
import za.co.moxomo.repository.elasticsearch.AlertPreferenceRepository;
import za.co.moxomo.services.VacancySearchService;
import za.co.moxomo.services.VacancySearchServiceImpl;

import javax.validation.Valid;

import static java.util.Optional.ofNullable;

@RestController
public class AlertServiceRestController {

    private VacancySearchService vacancySearchService;
    private AlertPreferenceRepository alertPreferenceRepository;


    public AlertServiceRestController(VacancySearchService vacancySearchService) {
        this.vacancySearchService = vacancySearchService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<AlertPreference> findById(@PathVariable("id") String id) {
        return ofNullable(alertPreferenceRepository.findById(id).get())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AlertPreference> create(@Valid @RequestBody AlertPreference preference) throws Exception {
        return ResponseEntity.ok(vacancySearchService.createSearchPreference(preference));
    }
}
