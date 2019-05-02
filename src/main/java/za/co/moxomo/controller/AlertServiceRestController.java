package za.co.moxomo.controller;

import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import za.co.moxomo.domain.AlertPreference;
import za.co.moxomo.dto.AlertDTO;
import za.co.moxomo.enums.PushAlert;
import za.co.moxomo.enums.SmsAlert;
import za.co.moxomo.repository.mongodb.AlertPreferenceRepository;
import za.co.moxomo.services.VacancySearchService;

import javax.validation.Valid;

import java.util.Arrays;
import java.util.UUID;

import static java.util.Optional.ofNullable;

@RestController
@RequestMapping("/alerts")
@Slf4j
public class AlertServiceRestController {

    private VacancySearchService vacancySearchService;
    private AlertPreferenceRepository alertPreferenceRepository;
    private ModelMapper modelMapper;


    @Autowired
    public AlertServiceRestController(VacancySearchService vacancySearchService, AlertPreferenceRepository alertPreferenceRepository, ModelMapper modelMapper) {
        this.vacancySearchService = vacancySearchService;
        this.alertPreferenceRepository=alertPreferenceRepository;
        this.modelMapper=modelMapper;
    }

    @GetMapping("/{id}")
    public ResponseEntity<AlertPreference> findById(@PathVariable("id") String id) {
        return ofNullable(alertPreferenceRepository.findById(id).get())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping(value = "/create",consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AlertPreference> create(@Valid @RequestBody AlertDTO alertDTO) throws Exception {
        AlertPreference preference = modelMapper.map(alertDTO, AlertPreference.class);
        preference.setAlertPreferenceId(UUID.randomUUID().toString());
        AlertPreference.Criteria criteria=  AlertPreference.Criteria.builder()
                .jobTitle(alertDTO.getTitle()).location(alertDTO.getLocation()).build();
        preference.setCriteria(criteria);
        preference.setPushAlert(alertDTO.isPush());
        preference.setSmsAlert(alertDTO.isSms());


        log.info("AlertPreference {}", preference);
        return ResponseEntity.ok(vacancySearchService.createSearchPreference(preference));
    }
}
