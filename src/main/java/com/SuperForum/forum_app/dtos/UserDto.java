package com.SuperForum.forum_app.dtos;

import com.SuperForum.forum_app.enums.UserRole;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserDto {

    private Long userId;

    private String username;

    private String email;

    private String password;

    private LocalDateTime createdAt;

    private LocalDateTime lastLogin;

    private UserRole userRole;
}
