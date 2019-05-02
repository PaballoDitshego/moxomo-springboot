package za.co.moxomo.repository.mongodb;

import org.springframework.data.repository.CrudRepository;
import za.co.moxomo.domain.AlertPreference;

import java.util.List;

public interface AlertPreferenceRepository extends CrudRepository<AlertPreference, String> {

    List<AlertPreference> findAlertPreferenceByMobileNumber(String mobileNumber);


}