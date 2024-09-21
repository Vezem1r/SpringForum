package com.SuperForum.forum_app.services.auth.jwt;

import com.SuperForum.forum_app.entities.User;
import com.SuperForum.forum_app.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String emailOrUsername) throws UsernameNotFoundException {
        Optional<User> optionalUser;

        if (emailOrUsername.contains("@")){
            optionalUser = userRepository.findByEmail(emailOrUsername);
        } else {
            optionalUser = userRepository.findByUsername(emailOrUsername);
        }
        if(optionalUser.isEmpty()) throw new UsernameNotFoundException("User not found " + emailOrUsername);
        User user = optionalUser.get();
        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), new ArrayList<>());
    }
}
