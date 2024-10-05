package com.back_end.forum.service;

import com.back_end.forum.dto.TopicDto;
import com.back_end.forum.dto.UserProfileDto;
import com.back_end.forum.model.Topic;
import com.back_end.forum.model.User;
import com.back_end.forum.repository.CommentRepository;
import com.back_end.forum.repository.TopicRepository;
import com.back_end.forum.repository.UserRepository;
import com.back_end.forum.service.auth.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.Multipart;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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

    private final String UPLOAD_DIR = "src/main/resources/static/avatars";

    public List<User> allUsers(){
        List<User> users = new ArrayList<>();
        userRepository.findAll().forEach(users::add);
        return users;
    }

    public UserProfileDto getProfileByUsername(String username){
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserProfileDto profileDto = new UserProfileDto();
        profileDto.setUserId(user.getUserId());
        profileDto.setUsername(user.getUsername());
        profileDto.setCreatedAt(user.getCreatedAt());
        profileDto.setLastLogin(user.getLastLogin());
        profileDto.setProfilePicture(user.getProfilePicture());

        int commentCount = commentRepository.countByUser(user);
        int topicCount = topicRepository.countByUser(user);
        profileDto.setCommentCount(commentCount);
        profileDto.setTopicCount(topicCount);

        return profileDto;
    }

    public String uploadAvatar(Long userId, MultipartFile file) throws IOException{
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        if(user.getProfilePicture() != null){
            Path oldAvatarPath = Paths.get(UPLOAD_DIR).resolve(user.getProfilePicture());
            Files.deleteIfExists(oldAvatarPath);
        }

        String avatarFileName = "avatar_" + userId + "_" + System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path avatarPath = Paths.get(UPLOAD_DIR).resolve(avatarFileName);
        Files.copy(file.getInputStream(), avatarPath);

        user.setProfilePicture(avatarFileName);
        userRepository.save(user);

        return avatarFileName;
    }


    public void initiatePasswordResetByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User with this email not found"));

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
        String resetCode;

        do {
            int code = random.nextInt(900000) + 100000;
            resetCode = String.valueOf(code);
        } while (userRepository.existsByPasswordResetCode(resetCode));

        return resetCode;
    }

    public boolean changeUsername(Long userId, String newUsername) {
        if (userRepository.existsByUsername(newUsername)) {
            return false;
        }
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        user.setUsername(newUsername);
        userRepository.save(user);
        return true;
    }
}
