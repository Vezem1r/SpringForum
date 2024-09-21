package com.SuperForum.forum_app.controllers;

import com.SuperForum.forum_app.dtos.AuthenticationRequest;
import com.SuperForum.forum_app.dtos.AuthenticationResponse;
import com.SuperForum.forum_app.dtos.SignupRequest;
import com.SuperForum.forum_app.dtos.UserDto;
import com.SuperForum.forum_app.entities.User;
import com.SuperForum.forum_app.repositories.UserRepository;
import com.SuperForum.forum_app.services.auth.AuthService;
import com.SuperForum.forum_app.services.auth.jwt.UserDetailsServiceImpl;
import com.SuperForum.forum_app.util.JwtUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.neo4j.Neo4jProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    private final AuthenticationManager authenticationManager;

    private final UserDetailsServiceImpl userDetailsService;

    private final JwtUtil jwtUtil;

    private final UserRepository userRepository;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignupRequest signupRequest){
        UserDto createdUserDto = authService.createUser(signupRequest);

        if (createdUserDto == null){
            return new ResponseEntity<>("User not created", HttpStatus.CONFLICT);
        }

        return new ResponseEntity<>(createdUserDto, HttpStatus.CREATED);
    }

    @PostMapping("/signin")
    public AuthenticationResponse createAuthToken(@RequestBody AuthenticationRequest authenticationRequest, HttpServletResponse response) throws IOException {
        try{
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authenticationRequest.getEmailOrUsername(), authenticationRequest.getPassword()));
        } catch (BadCredentialsException e){
            throw new BadCredentialsException("Incorrect username or password");
        } catch (DisabledException disabledException){
            response.sendError(HttpServletResponse.SC_NOT_FOUND,"User not active");
            return null;
        }
        final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getEmailOrUsername());
        final String jwt = jwtUtil.generateToken(userDetails);
        Optional<User> optionalUser = userRepository.findByEmail(userDetails.getUsername());
        AuthenticationResponse authenticationResponse = new AuthenticationResponse();

        if(optionalUser.isPresent()){
            User user = optionalUser.get();
            user.setLastLogin(LocalDateTime.now());
            userRepository.save(user);

            authenticationResponse.setJwt(jwt);
            authenticationResponse.setUserRole(optionalUser.get().getUserRole());
            authenticationResponse.setUserId(optionalUser.get().getUserId());
        }
        return authenticationResponse;
    }
}
