package za.co.moxomo.repository.mongodb;

import org.springframework.data.mongodb.repository.MongoRepository;
import za.co.moxomo.domain.Notification;

public interface NotificationRepository extends MongoRepository<Notification, String> {

}
