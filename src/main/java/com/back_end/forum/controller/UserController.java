package com.back_end.forum.controller;

import com.back_end.forum.dto.ChangePasswordDto;
import com.back_end.forum.dto.UserProfileDto;
import com.back_end.forum.model.User;
import com.back_end.forum.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/users")
@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/profile")
    public ResponseEntity<UserProfileDto> userProfile(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();
        UserProfileDto profile = userService.getUserProfile(currentUser.getUserId());
        return ResponseEntity.ok(profile);
    }
    @PostMapping("/password-reset/request")
    public ResponseEntity<String> requestPasswordReset(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();
        userService.initiatePasswordReset(currentUser.getUserId());
        return ResponseEntity.ok("Password reset code has been sent to your email.");
    }

    @PostMapping("/password-reset/confirm")
    public ResponseEntity<String> confirmPasswordReset(@RequestBody ChangePasswordDto changePasswordDto){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();
        userService.resetPassword(currentUser.getUserId(), changePasswordDto.getResetCode(), changePasswordDto.getNewPassword());
        return ResponseEntity.ok("Password has been successfully reset.");
    }
}
