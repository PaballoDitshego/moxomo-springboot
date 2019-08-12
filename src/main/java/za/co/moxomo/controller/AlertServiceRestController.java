package za.co.moxomo.controller;

import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import za.co.moxomo.domain.AlertPreference;
import za.co.moxomo.domain.FCMToken;
import za.co.moxomo.domain.GeoLocation;
import za.co.moxomo.domain.Notification;
import za.co.moxomo.dto.AlertDTO;
import za.co.moxomo.enums.AlertRoute;
import za.co.moxomo.enums.AlertType;
import za.co.moxomo.repository.mongodb.AlertPreferenceRepository;
import za.co.moxomo.repository.mongodb.FcmTokenRepository;
import za.co.moxomo.services.GeoService;
import za.co.moxomo.services.GeoServiceImpl;
import za.co.moxomo.services.NotificationSendingService;
import za.co.moxomo.services.VacancySearchService;

import javax.validation.Valid;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static java.util.Optional.ofNullable;

@RestController
@RequestMapping("rest/alerts")
@Slf4j
public class AlertServiceRestController {

    private static double defaultLat = -28.4793;
    private static double defaultLon = 24.6727;

    private VacancySearchService vacancySearchService;
    private AlertPreferenceRepository alertPreferenceRepository;
    private FcmTokenRepository fcmTokenRepository;
    private ModelMapper modelMapper;
    private GeoService geoService;


    @Autowired
    public AlertServiceRestController(VacancySearchService vacancySearchService, AlertPreferenceRepository alertPreferenceRepository, FcmTokenRepository fcmTokenRepository, ModelMapper modelMapper, GeoService geoService) {
        this.vacancySearchService = vacancySearchService;
        this.alertPreferenceRepository = alertPreferenceRepository;
        this.modelMapper = modelMapper;
        this.fcmTokenRepository = fcmTokenRepository;
        this.geoService = geoService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<AlertPreference> findById(@PathVariable("id") String id) {
        return ofNullable(alertPreferenceRepository.findById(id).get())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping(value = "/create", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AlertPreference> create(@Valid @RequestBody AlertDTO alertDTO) throws Exception {
        log.debug("Alert create request {}", alertDTO.toString());
        double geoPoint[] = new double[2];
        String[] locale = alertDTO.getLocation().split(",");
        if (locale.length > 1) {
            GeoLocation geoLocation = geoService.getByCityAndProvince(locale[0].trim(), locale[1].trim());
            if (Objects.nonNull(geoLocation)) {
                geoPoint[0] = geoLocation.getLatitude();
                geoPoint[1] = geoLocation.getLongitude();
            } else {
                geoLocation = geoService.getGeoLocation(alertDTO.getLocation());
                if (Objects.nonNull(geoLocation)) {
                    geoPoint[0] = geoLocation.getLatitude();
                    geoPoint[1] = geoLocation.getLongitude();
                } else {
                    geoPoint[0] = defaultLat;
                    geoPoint[1] = defaultLon;
                }
            }
        }
        AlertPreference preference = modelMapper.map(alertDTO, AlertPreference.class);
        preference.setId(UUID.randomUUID().toString());
        AlertPreference.Criteria criteria = AlertPreference.Criteria.builder()
                .keyword(alertDTO.getKeyword())
                .location(alertDTO.getLocation())
                .point(geoPoint).build();

        preference.setMobileNumber(alertDTO.getMobileNumber());
        preference.setGcmToken(alertDTO.getGcmToken());
        preference.setCriteria(criteria);
        preference.setPushAlert(alertDTO.isPush());
        preference.setSmsAlert(alertDTO.isSms());

        log.debug("AlertPreference {}", preference);
        return ResponseEntity.ok(vacancySearchService.createSearchPreference(preference));
    }

    @DeleteMapping(value = "/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable("id") String id) throws Exception {
        log.debug("Alert delete request {}", id);
        alertPreferenceRepository.deleteById(id);
        return ResponseEntity.ok("Done");
    }

    @PostMapping(value = "/delete-all", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> deleteAll(@RequestBody List<String> ids) throws Exception {
        ids.stream().forEach(id -> alertPreferenceRepository.deleteById(id));
        return ResponseEntity.ok("Done");
    }


    @PostMapping(value = "/fcmtoken")
    public ResponseEntity<Boolean> saveFcmToken(@Valid @RequestParam String newToken, @RequestParam(required = false) String oldToken) throws Exception {
        log.info("Recieved fcmtoken save request, newToken {}, oldToken {}", newToken, oldToken);
        if (Objects.nonNull(oldToken)) {
            List<AlertPreference> alertPreferences = alertPreferenceRepository.findAlertPreferenceByGcmToken(oldToken);
            alertPreferences.stream().forEach(alertPreference -> {
                alertPreference.setGcmToken(newToken);
                alertPreferenceRepository.save(alertPreference);
            });
        }
        FCMToken fcmToken;
        if (Objects.nonNull(fcmToken = fcmTokenRepository.findByToken(oldToken))) {
            fcmToken.setToken(newToken);
        } else {
            fcmToken = new FCMToken();
            fcmToken.setToken(newToken);
        }
        fcmTokenRepository.save(fcmToken);

        return ResponseEntity.ok(true);

    }


}
