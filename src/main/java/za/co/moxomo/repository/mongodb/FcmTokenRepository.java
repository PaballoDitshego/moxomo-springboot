package za.co.moxomo.repository.mongodb;

import org.springframework.data.mongodb.repository.MongoRepository;
import za.co.moxomo.domain.FCMToken;

public interface FcmTokenRepository extends MongoRepository<FCMToken, String> {
    FCMToken findByToken(String token);
}

