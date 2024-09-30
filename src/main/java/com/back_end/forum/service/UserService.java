package com.back_end.forum.service;

import com.back_end.forum.dto.UserProfileDto;
import com.back_end.forum.model.User;
import com.back_end.forum.repository.CommentRepository;
import com.back_end.forum.repository.TopicRepository;
import com.back_end.forum.repository.UserRepository;
import com.back_end.forum.service.auth.EmailService;
import jakarta.mail.MessagingException;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final CommentRepository commentRepository;

    private final TopicRepository topicRepository;

    private final EmailService emailService;

    private final PasswordEncoder passwordEncoder;

    public List<User> allUsers(){
        List<User> users = new ArrayList<>();
        userRepository.findAll().forEach(users::add);
        return users;
    }

    public UserProfileDto getUserProfile(Long userId){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserProfileDto profileDto = new UserProfileDto();
        profileDto.setUserId(user.getUserId());
        profileDto.setUsername(user.getUsername());
        profileDto.setEmail(user.getEmail());
        profileDto.setCreatedAt(user.getCreatedAt());
        profileDto.setLastLogin(user.getLastLogin());

        int commentCount = commentRepository.countByUser(user);
        int topicCount = topicRepository.countByUser(user);
        profileDto.setCommentCount(commentCount);
        profileDto.setTopicCount(topicCount);

        return profileDto;
    }

    public void initiatePasswordReset(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String resetCode = generateResetCode();
        user.setPasswordResetCode(resetCode);
        user.setPasswordResetCodeExpiredAt(LocalDateTime.now().plusMinutes(15));
        userRepository.save(user);

        String subject = "Password Reset Request";
        String text = "Your password reset code is: " + resetCode;
        try {
            emailService.sendVerificationEmail(user.getEmail(), subject, text);
        } catch (MessagingException e) {
            System.out.println("Failed to send email: " + e.getMessage());
            throw new RuntimeException("Failed to send password reset email", e);
        }
    }

    public void resetPassword(Long userId, String resetCode, String newPassword){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getPasswordResetCodeExpiredAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Reset code has expired");
        }

        if (!user.getPasswordResetCode().equals(resetCode)) {
            throw new RuntimeException("Invalid reset code");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setPasswordResetCode(null);
        user.setPasswordResetCodeExpiredAt(null);
        userRepository.save(user);
    }

    private String generateResetCode() {
        Random random = new Random();
        int code = random.nextInt(900000) + 100000;
        return String.valueOf(code);
    }

}
