package za.co.moxomo.services;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import za.co.moxomo.config.security.JwtTokenProvider;
import za.co.moxomo.exception.CustomException;
import za.co.moxomo.model.User;
import za.co.moxomo.repository.mongodb.UserRepository;


@Component
public class UserService implements UserDetailsService {


    private UserRepository userRepository;


    private PasswordEncoder passwordEncoder;



    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        return org.springframework.security.core.userdetails.User
                .withUsername(username)
                .password(user.getPassword())
                .authorities(user.getRoles())
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(false)
                .build();
    }

    public User signup(User user) {
        if (null == userRepository.findByUsername(user.getUsername())) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));

            userRepository.save(user);
            return user;
        } else {
          //  throw new RuntimeException();
           throw new CustomException("Username is already in use", HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }
}