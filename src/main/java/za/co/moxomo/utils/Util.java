package za.co.moxomo.utils;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import za.co.moxomo.domain.Alert;
import za.co.moxomo.domain.AlertPreference;
import za.co.moxomo.domain.Vacancy;
import za.co.moxomo.enums.AlertRoute;


import java.time.Instant;
import java.util.*;

/**
 * Created by paballo on 2016/11/16.
 */
public class Util {

    private static final Logger logger = LoggerFactory.getLogger(Util.class);

    public static boolean validate(Vacancy vacancy) {
        boolean valid = (Objects.nonNull(vacancy.getId())
                && Objects.nonNull(vacancy.getId())
                && Objects.nonNull(vacancy.getJobTitle()) && !vacancy.getJobTitle().isEmpty()
                && Objects.nonNull(vacancy.getDescription()) && !vacancy.getDescription().isEmpty()
                && Objects.nonNull(vacancy.getLocation()) && !vacancy.getLocation().isEmpty()
                && Objects.nonNull(vacancy.getAdvertDate())
                && Objects.nonNull(vacancy.getImageUrl())) && !vacancy.getImageUrl().isEmpty()
                && Objects.nonNull(vacancy.getUrl()) && !vacancy.getUrl().isEmpty()
                && Objects.nonNull(vacancy.getCompany()) && !vacancy.getCompany().isEmpty()
                && Objects.nonNull(vacancy.getOfferId()) && !vacancy.getOfferId().isEmpty();
        if (!valid) {
            logger.info("Invalid {}, Object {}", vacancy.getUrl(), vacancy.toString());
        }
        return valid;
    }

    public static Alert generateAlert(Vacancy vacancy, AlertPreference alertPreference) {
        Alert alert = Alert.builder().alertId(UUID.randomUUID().toString())
                .description(vacancy.getDescription())
                .createdDateTime(Instant.now()).advertDate(vacancy.getAdvertDate())
                .imageUrl(vacancy.getImageUrl()).entityId(vacancy.getId()).entityType(Vacancy.class.getTypeName())
                .location(vacancy.getLocation()).route((alertPreference.isPushAlert())?
                        AlertRoute.FCM.getRoute():AlertRoute.SMS.getRoute()).build();

        return alert;
    }
}
