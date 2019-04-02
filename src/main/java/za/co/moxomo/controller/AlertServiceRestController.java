package za.co.moxomo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import za.co.moxomo.domain.AlertPreference;
import za.co.moxomo.repository.mongodb.AlertPreferenceRepository;
import za.co.moxomo.services.VacancySearchService;

import javax.validation.Valid;

import static java.util.Optional.ofNullable;

@RestController
@RequestMapping("/alerts")
public class AlertServiceRestController {

    private VacancySearchService vacancySearchService;
    private AlertPreferenceRepository alertPreferenceRepository;


    @Autowired
    public AlertServiceRestController(VacancySearchService vacancySearchService, AlertPreferenceRepository alertPreferenceRepository) {
        this.vacancySearchService = vacancySearchService;
        this.alertPreferenceRepository=alertPreferenceRepository;
    }

    @GetMapping("/{id}")
    public ResponseEntity<AlertPreference> findById(@PathVariable("id") String id) {
        return ofNullable(alertPreferenceRepository.findById(id).get())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping(value = "/create",consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AlertPreference> create(@Valid @RequestBody AlertPreference preference) throws Exception {
        return ResponseEntity.ok(vacancySearchService.createSearchPreference(preference));
    }
}
