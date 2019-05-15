package za.co.moxomo.repository.mongodb;

import org.springframework.data.mongodb.repository.MongoRepository;
import za.co.moxomo.domain.AlertPreference;

import java.util.List;

public interface AlertPreferenceRepository extends MongoRepository<AlertPreference, String> {

    List<AlertPreference> findAlertPreferenceByMobileNumber(String mobileNumber);
    List<AlertPreference> findAlertPreferenceByGcmToken(String gcmToken);
    AlertPreference findAlertPreferenceById(String Id);


}