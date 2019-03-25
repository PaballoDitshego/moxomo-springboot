package za.co.moxomo.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import za.co.moxomo.model.User;

public interface UserRepository  extends MongoRepository<User, String> {
}
