package za.co.moxomo.repository.mongodb;

import org.springframework.data.repository.CrudRepository;
import za.co.moxomo.domain.AlertPreference;

public interface AlertPreferenceRepository extends CrudRepository<AlertPreference, String> {
}
