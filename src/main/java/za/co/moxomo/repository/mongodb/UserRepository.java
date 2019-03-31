package za.co.moxomo.repository.mongodb;

import org.springframework.data.mongodb.repository.MongoRepository;
import za.co.moxomo.domain.User;

public interface UserRepository  extends MongoRepository<User, String> {
    User findByUsername(String username);
}
