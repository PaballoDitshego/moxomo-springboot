package za.co.moxomo.services;

import za.co.moxomo.domain.User;

public interface UserService {

    String authenticate(String username, String password);

    String registerUser(User user);

    String refresh(String username);
}
