package com.SuperForum.forum_app.dtos;

import lombok.Data;

@Data
public class SignupRequest {

    private String username;

    private String email;

    private String password;
}
