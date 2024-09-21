package com.SuperForum.forum_app.services.auth.jwt;

import com.SuperForum.forum_app.entities.User;
import com.SuperForum.forum_app.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService, UserService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String emailOrUsername) throws UsernameNotFoundException {
        Optional<User> optionalUser;

        if (emailOrUsername.contains("@")) {
            System.out.println("Loading user by email: " + emailOrUsername);
            optionalUser = userRepository.findByEmail(emailOrUsername);
        } else {
            System.out.println("Loading user by username: " + emailOrUsername);
            optionalUser = userRepository.findByUsername(emailOrUsername);
        }

        if (optionalUser.isEmpty()) {
            System.out.println("User not found: " + emailOrUsername);
            throw new UsernameNotFoundException("User not found " + emailOrUsername);
        }

        User user = optionalUser.get();
        System.out.println("Found user: " + user.getUserRole());

        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" + user.getUserRole()));

        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), authorities);
    }

    @Override
    public UserDetailsService userDetailsService() {
        return this;
    }
}
