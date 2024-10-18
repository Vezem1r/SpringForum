package com.back_end.forum.controller;

import com.back_end.forum.dto.Auth.LoginUserDto;
import com.back_end.forum.dto.Auth.RegisterUserDto;
import com.back_end.forum.dto.Auth.VerifyUserDto;
import com.back_end.forum.dto.ChangePasswordDto;
import com.back_end.forum.model.User;
import com.back_end.forum.repository.UserRepository;
import com.back_end.forum.responses.LoginResponse;
import com.back_end.forum.service.UserService;
import com.back_end.forum.service.auth.AuthService;
import com.back_end.forum.service.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RequestMapping("/auth")
@RestController
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final JwtService jwtService;
    private final AuthService authService;
    private final UserRepository userRepository;
    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<User> signup(@RequestBody RegisterUserDto registerUserDto){
        log.info("Received register request: {}", registerUserDto);

        User registeredUser = authService.signup(registerUserDto);
        return ResponseEntity.ok(registeredUser);
    }

    @PostMapping("/signin")
    public ResponseEntity<LoginResponse> signin(@RequestBody LoginUserDto loginUserDto){
        log.info("Received login request: {}", loginUserDto);

        User authenticatedUser = authService.authenticate(loginUserDto);
        authenticatedUser.setLastLogin(LocalDateTime.now());
        userRepository.save(authenticatedUser);
        String jwtToken = jwtService.generateToken(authenticatedUser);
        LoginResponse loginResponse = new LoginResponse(jwtToken, jwtService.getExpirationTime(), "Login successful");
        return ResponseEntity.ok(loginResponse);
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyUser(@RequestBody VerifyUserDto verifyUserDto){
        try {
            authService.verifyUser(verifyUserDto);
            return ResponseEntity.ok(Map.of("message", "Account verified successfully")); // Return as JSON
        } catch (RuntimeException err) {
            log.error("Verification error: {}", err.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", err.getMessage()));
        }
    }


    @PostMapping("/resend")
    public ResponseEntity<?> resendVerificationCode(@RequestParam String email){
        try{
            authService.resendVerificationCode(email);
            return ResponseEntity.ok("Verification code resend");
        } catch (RuntimeException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/password-reset/request")
    public ResponseEntity<String> requestPasswordReset(@RequestParam("email") String email){
        try {
            userService.initiatePasswordResetByEmail(email);
            return ResponseEntity.ok("Password reset code has been sent to your email.");
        } catch (RuntimeException e) {
            log.error("Error sending verification code: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @PostMapping("/password-reset/confirm")
    public ResponseEntity<String> confirmPasswordReset(@RequestBody ChangePasswordDto changePasswordDto) {
        User user = userRepository.findByPasswordResetCode(changePasswordDto.getResetCode())
                .orElseThrow(() -> new RuntimeException("Invalid reset code or user not found"));

        userService.resetPassword(user.getUserId(), changePasswordDto.getResetCode(), changePasswordDto.getNewPassword());

        return ResponseEntity.ok("Password has been successfully reset.");
    }
}
