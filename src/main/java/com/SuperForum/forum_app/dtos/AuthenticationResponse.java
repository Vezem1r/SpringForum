package com.SuperForum.forum_app.dtos;

import com.SuperForum.forum_app.enums.UserRole;
import lombok.Data;

@Data
public class AuthenticationResponse {

    private String jwt;

    private UserRole userRole;

    private Long userId;

}
