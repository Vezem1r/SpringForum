package com.back_end.forum.service;

import com.back_end.forum.dto.UserProfileDto;
import com.back_end.forum.model.Comment;
import com.back_end.forum.model.Topic;
import com.back_end.forum.model.User;
import com.back_end.forum.model.enums.RolesEnum;
import com.back_end.forum.repository.CommentRepository;
import com.back_end.forum.repository.TopicRepository;
import com.back_end.forum.repository.UserRepository;
import com.back_end.forum.service.auth.EmailService;
import com.back_end.forum.utils.FolderUtils;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final TopicRepository topicRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    private final static String UPLOAD_DIR = "static/avatars";

    @Value("${admin.password}")
    private String password;

    public UserService(
            UserRepository userRepository,
            CommentRepository commentRepository,
            TopicRepository topicRepository,
            EmailService emailService,
            PasswordEncoder passwordEncoder) {
        this.userRepository  = userRepository;
        this.commentRepository = commentRepository;
        this.topicRepository = topicRepository;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;

        FolderUtils.createDirectories(UPLOAD_DIR);
    }

    public List<User> allUsers(){
        log.info("Fetching all users");
        List<User> users = new ArrayList<>();
        userRepository.findAll().forEach(users::add);
        log.info("Number of users retrieved: {}", users.size());
        return users;
    }

    public UserProfileDto getProfileByUsername(String username){
        log.info("Fetching profile for user: {}", username);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.error("User not found: {}", username);
                    return new RuntimeException("User not found");
                });

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

        int totalRating = 0;

        List<Topic> topics = topicRepository.findByUser(user);
        for (Topic topic : topics) {
            totalRating += topic.getRating();
        }

        List<Comment> comments = commentRepository.findByUser(user);
        for (Comment comment : comments) {
            totalRating += comment.getRating();
        }

        profileDto.setRating(totalRating);

        log.info("Profile fetched for user: {}, Comment Count: {}, Topic Count: {}, Total Rating: {}",
                username, commentCount, topicCount, totalRating);

        return profileDto;
    }

    public String uploadAvatar(Long userId, MultipartFile file) throws IOException{
        log.info("Uploading avatar for userId: {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("User not found: {}", userId);
                    return new RuntimeException("User not found");
                });

        if(user.getProfilePicture() != null){
            Path oldAvatarPath = Paths.get(UPLOAD_DIR).resolve(user.getProfilePicture());
            Files.deleteIfExists(oldAvatarPath);
            log.info("Deleted old avatar: {}", user.getProfilePicture());
        }

        String avatarFileName = "avatar_" + userId + "_" + System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path avatarPath = Paths.get(UPLOAD_DIR).resolve(avatarFileName);
        Files.copy(file.getInputStream(), avatarPath);

        user.setProfilePicture(avatarFileName);
        userRepository.save(user);
        log.info("Uploaded new avatar: {}", avatarFileName);

        return avatarFileName;
    }


    public void initiatePasswordResetByEmail(String email) {
        log.info("Initiating password reset for email: {}", email);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("User with this email not found: {}", email);
                    return new RuntimeException("User with this email not found");
                });

        String resetCode = generateResetCode();
        user.setPasswordResetCode(resetCode);
        user.setPasswordResetCodeExpiredAt(LocalDateTime.now().plusMinutes(15));
        userRepository.save(user);

        String subject = "Password Reset Request";
        String text = "Your password reset code is: " + resetCode;
        try {
            emailService.sendVerificationEmail(user.getEmail(), subject, text);
            log.info("Password reset email sent to: {}", user.getEmail());
        } catch (MessagingException e) {
            log.error("Failed to send email to {}: {}", user.getEmail(), e.getMessage());
            throw new RuntimeException("Failed to send password reset email", e);
        }
    }

    public void resetPassword(Long userId, String resetCode, String newPassword){
        log.info("Resetting password for userId: {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("User not found: {}", userId);
                    return new RuntimeException("User not found");
                });

        if (user.getPasswordResetCodeExpiredAt().isBefore(LocalDateTime.now())) {
            log.error("Reset code has expired for userId: {}", userId);
            throw new RuntimeException("Reset code has expired");
        }

        if (!user.getPasswordResetCode().equals(resetCode)) {
            log.error("Invalid reset code for userId: {}. Provided: {}", userId, resetCode);
            throw new RuntimeException("Invalid reset code");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setPasswordResetCode(null);
        user.setPasswordResetCodeExpiredAt(null);
        userRepository.save(user);
        log.info("Password reset successful for userId: {}", userId);

    }

    private String generateResetCode() {
        Random random = new Random();
        String resetCode;

        do {
            int code = random.nextInt(900000) + 100000;
            resetCode = String.valueOf(code);
        } while (userRepository.existsByPasswordResetCode(resetCode));

        log.info("Generated reset code: {}", resetCode);
        return resetCode;
    }

    public boolean changeUsername(Long userId, String newUsername) {
        log.info("Changing username for userId: {} to {}", userId, newUsername);
        if (userRepository.existsByUsername(newUsername)) {
            log.error("Username already exists: {}", newUsername);
            return false;
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("User not found: {}", userId);
                    return new RuntimeException("User not found");
                });
        user.setUsername(newUsername);
        userRepository.save(user);
        log.info("Username changed successfully for userId: {}", userId);
        return true;
    }

    public User findByUsername(String username) {
        log.info("Finding user by username: {}", username);
        return userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.error("User not found: {}", username);
                    return new IllegalArgumentException("User not found");
                });
    }

    public User createGuest() {
        log.info("Creating guest");
        return createUser("Guest", RolesEnum.GUEST);
    }
    public User createAdmin() {
        log.info("Creating admin");
        return createUser("Admin", RolesEnum.ADMIN);
    }

    private User createUser(String name, RolesEnum role) {

        Optional<User> existingOfUser = userRepository.findByUsername(name);

        if(existingOfUser.isPresent()){
            log.error("Username already exists: {}", name);
            return  existingOfUser.get();
        }

        User user = new User();
        user.setUsername(name);
        user.setEmail(name + "_USER@forum.cz");
        user.setPassword(passwordEncoder.encode(name + "_" + password));
        user.setRole(role);
        user.setCreatedAt(LocalDateTime.now());
        user.setLastLogin(LocalDateTime.now());
        user.setEnabled(true);

        log.info("Created user: {}", user);

        return userRepository.save(user);
    }

    public void createUsers(int amount) {
        log.info("Creating {} random users", amount);

        List<String> predefinedUsernames = Arrays.asList(
                "CoolCat", "ThunderBolt", "MagicDragon", "Sunshine", "NightHawk",
                "SuperStar", "SkyWalker", "PixelWizard", "FireFox", "OceanWave"
        );

        Random random = new Random();

        for (int i = 0; i < amount; i++) {
            String randomUsername = predefinedUsernames.get(random.nextInt(predefinedUsernames.size()));

            createUser(randomUsername, RolesEnum.USER);
        }
    }
}
