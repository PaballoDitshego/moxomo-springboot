package za.co.moxomo.controller;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import za.co.moxomo.UserDTO;
import za.co.moxomo.domain.User;
import za.co.moxomo.services.UserService;

import java.util.Objects;


@RestController
@RequestMapping("/users")
public class UserServiceRestController {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceRestController.class);

    private UserService userService;

    private ModelMapper modelMapper;

    @Autowired
    public UserServiceRestController(UserService userService, ModelMapper modelMapper) {
        this.userService = userService;
        this.modelMapper = modelMapper;
    }

    @PostMapping("/signup")
    public String signup(@RequestBody UserDTO userDTO) {
        Objects.requireNonNull(userDTO);
        User user = modelMapper.map(userDTO, User.class);

        user.getFcmTokens().add(userDTO.getFcmToken());
        return userService.registerUser(user);
    }
}
