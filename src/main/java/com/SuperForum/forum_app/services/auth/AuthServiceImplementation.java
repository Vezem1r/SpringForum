package com.SuperForum.forum_app.services.auth;

import com.SuperForum.forum_app.dtos.SignupRequest;
import com.SuperForum.forum_app.dtos.UserDto;
import com.SuperForum.forum_app.entities.User;
import com.SuperForum.forum_app.enums.UserRole;
import com.SuperForum.forum_app.repositories.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthServiceImplementation implements AuthService{

    private final UserRepository userRepository;

    @PostConstruct
    public void createAdminAccount(){
        User adminAccount = userRepository.findByUserRole(UserRole.ADMIN);
        if(adminAccount == null) {
            User user = new User();
            user.setUsername("admin");
            user.setEmail("admin@gmail.com");
            user.setPassword(new BCryptPasswordEncoder().encode("admin"));
            user.setUserRole(UserRole.ADMIN);
            user.setCreatedAt(LocalDateTime.now());
            user.setLastLogin(LocalDateTime.now());
            userRepository.save(user);
        }
    }

    @Override
    public UserDto createUser(SignupRequest signupRequest) {

        Optional<User> existingUserByUsername = userRepository.findByUsername(signupRequest.getUsername());
        Optional<User> existingUserByEmail = userRepository.findByEmail(signupRequest.getEmail());

        if (existingUserByUsername.isPresent() || existingUserByEmail.isPresent()) {
            return null;
        }

        User user = new User();
        user.setUsername(signupRequest.getUsername());
        user.setEmail(signupRequest.getEmail());
        user.setPassword(new BCryptPasswordEncoder().encode(signupRequest.getPassword()));
        user.setUserRole(UserRole.USER);
        user.setCreatedAt(LocalDateTime.now());
        user.setLastLogin(LocalDateTime.now());
        User createdUser = userRepository.save(user);

        UserDto createdUserDto = new UserDto();
        createdUserDto.setUserId(createdUser.getUserId());
        createdUserDto.setUsername(createdUser.getUsername());
        createdUserDto.setEmail(createdUser.getEmail());
        createdUserDto.setUserRole(createdUser.getUserRole());
        createdUserDto.setCreatedAt(createdUser.getCreatedAt());
        createdUserDto.setLastLogin(createdUser.getLastLogin());
        return createdUserDto;
    }

}
