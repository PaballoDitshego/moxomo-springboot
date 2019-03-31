package za.co.moxomo.repository.elasticsearch;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import za.co.moxomo.domain.AlertPreference;

public interface AlertPreferenceRepository extends ElasticsearchRepository<AlertPreference, String> {
}
