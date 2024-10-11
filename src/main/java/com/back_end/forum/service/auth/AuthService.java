package com.back_end.forum.service.auth;

import com.back_end.forum.dto.Auth.LoginUserDto;
import com.back_end.forum.dto.Auth.RegisterUserDto;
import com.back_end.forum.dto.Auth.VerifyUserDto;
import com.back_end.forum.exception.BadRequest;
import com.back_end.forum.model.User;
import com.back_end.forum.repository.UserRepository;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    private final EmailService emailService;

    public User signup(RegisterUserDto registerUserDto) {
        log.info("Attempting to sign up user: {}", registerUserDto.getUsername());

        Optional<User> existingUserByUsername = userRepository.findByUsername(registerUserDto.getUsername());
        if (existingUserByUsername.isPresent()) {
            User userByUsername = existingUserByUsername.get();
            if (userByUsername.isEnabled()) {
                log.error("User with username '{}' already exists.", registerUserDto.getUsername());
                throw new BadRequest("User with this username already exists.");
            } else {
                userRepository.delete(userByUsername);
                log.info("Deleted disabled user with username: {}", registerUserDto.getUsername());
            }
        }

        Optional<User> existingUserByEmail = userRepository.findByEmail(registerUserDto.getEmail());
        if (existingUserByEmail.isPresent()) {
            User userByEmail = existingUserByEmail.get();
            if (userByEmail.isEnabled()) {
                log.error("User with email '{}' already exists.", registerUserDto.getEmail());
                throw new BadRequest("User with this email already exists.");
            } else {
                userRepository.delete(userByEmail);
                log.info("Deleted disabled user with email: {}", registerUserDto.getEmail());
            }
        }

        User user = new User();
        user.setUsername(registerUserDto.getUsername());
        user.setEmail(registerUserDto.getEmail());
        user.setPassword(passwordEncoder.encode(registerUserDto.getPassword()));
        user.setCreatedAt(LocalDateTime.now());
        user.setLastLogin(LocalDateTime.now());
        user.setVerificationCode(generateVerificationCode());
        user.setVerificationCodeExpiredAt(LocalDateTime.now().plusMinutes(15));
        user.setEnabled(false);
        sendVerificationEmail(user);
        log.info("User signed up successfully: {}", registerUserDto.getUsername());
        return userRepository.save(user);
    }

    public User authenticate(LoginUserDto loginUserDto) {
        String usernameOrEmail = loginUserDto.getUsernameOrEmail();
        log.info("Received login request for: {}", usernameOrEmail);

        Optional<User> optionalUser;

        if (usernameOrEmail.contains("@")) {
            log.info("Loading user by email: {}", usernameOrEmail);
            optionalUser = userRepository.findByEmail(usernameOrEmail);
        } else {
            log.info("Loading user by username: {}", usernameOrEmail);
            optionalUser = userRepository.findByUsername(usernameOrEmail);
        }

        if (optionalUser.isEmpty()) {
            log.error("User not found: {}", usernameOrEmail);
            throw new UsernameNotFoundException("User not found " + usernameOrEmail);
        }

        User user = optionalUser.get();

        if (!user.isEnabled()) {
            log.warn("Account not verified for user: {}", usernameOrEmail);
            throw new RuntimeException("Account not verified");
        }

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        usernameOrEmail,
                        loginUserDto.getPassword()
                )
        );
        log.info("User authenticated successfully: {}", usernameOrEmail);
        return user;
    }

    public void verifyUser(VerifyUserDto verifyUserDto) {
        Optional<User> optionalUser = userRepository.findByEmail(verifyUserDto.getEmail());
        if (optionalUser.isEmpty()) {
            log.error("User not found for verification: {}", verifyUserDto.getEmail());
            throw new RuntimeException("User not found");
        }

        User user = optionalUser.get();
        if (user.getVerificationCodeExpiredAt().isBefore(LocalDateTime.now())) {
            log.error("Verification code has expired for user: {}", verifyUserDto.getEmail());
            throw new RuntimeException("Verification code has expired");
        }
        if (user.getVerificationCode().equals(verifyUserDto.getVerificationCode())) {
            user.setEnabled(true);
            user.setVerificationCode(null);
            user.setVerificationCodeExpiredAt(null);
            userRepository.save(user);
            log.info("User verified successfully: {}", verifyUserDto.getEmail());
        } else {
            log.error("Invalid verification code for user: {}", verifyUserDto.getEmail());
            throw new RuntimeException("Invalid verification code");
        }
    }

    public void resendVerificationCode(String email) {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (user.isEnabled()) {
                log.warn("Account is already verified for user: {}", email);
                throw new RuntimeException("Account is already verified");
            }
            user.setVerificationCode(generateVerificationCode());
            user.setVerificationCodeExpiredAt(LocalDateTime.now().plusMinutes(5));
            sendVerificationEmail(user);
            userRepository.save(user);
            log.info("Resent verification code to user: {}", email);
        } else {
            log.error("User not found for resending verification code: {}", email);
            throw new RuntimeException("User not found");
        }
    }

    private String generateVerificationCode() {
        Random random = new Random();
        int code = random.nextInt(900000) + 100000;
        return String.valueOf(code);
    }
    private void sendVerificationEmail(User user) {
        String subject = "Account Verification";
        String verificationCode = "VERIFICATION CODE " + user.getVerificationCode();
        LocalDateTime expirationTime = user.getVerificationCodeExpiredAt();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String expiration = expirationTime.format(formatter);
        String htmlMessage = "<html>"
                + "<body style=\"font-family: Arial, sans-serif; margin: 0; padding: 0; background-color: #f5f5f5;\">"
                + "<div style=\"max-width: 600px; margin: auto; background-color: #ffffff; border-radius: 8px; box-shadow: 0 0 20px rgba(0, 0, 0, 0.1); padding: 20px;\">"
                + "<h2 style=\"color: #6a0dad; text-align: center;\">Welcome to Our App!</h2>"
                + "<p style=\"font-size: 16px; text-align: center; color: #333;\">We're excited to have you on board. To complete your registration, please enter the verification code below:</p>"
                + "<div style=\"background-color: #f0e6ff; padding: 20px; border-radius: 5px; text-align: center; border: 1px solid #6a0dad;\">"
                + "<h3 style=\"color: #6a0dad;\">Your Verification Code:</h3>"
                + "<p style=\"font-size: 24px; font-weight: bold; color: #6a0dad; padding: 10px; border: 2px dashed #6a0dad; border-radius: 5px; display: inline-block;\">"
                + verificationCode + "</p>"
                + "<p style=\"font-size: 14px; color: #555;\">This code is valid till: <strong style=\"color: #6a0dad;\">"
                + expiration
                + "</strong></p>"
                + "</div>"
                + "<p style=\"font-size: 14px; color: #555; text-align: center; margin-top: 20px;\">"
                + "If you did not request this email, please ignore it."
                + "</p>"
                + "<footer style=\"text-align: center; margin-top: 20px; font-size: 12px; color: #aaa;\">"
                + "&copy; " + LocalDate.now().getYear() + " Forum application. All rights reserved."
                + "</footer>"
                + "</div>"
                + "</div>"
                + "</body>"
                + "</html>";
        try {
            emailService.sendVerificationEmail(user.getEmail(), subject, htmlMessage);
            log.info("Verification email sent to: {}", user.getEmail());
        } catch (MessagingException e) {
            log.error("Failed to send verification email to: {}", user.getEmail(), e);
        }
    }
}
