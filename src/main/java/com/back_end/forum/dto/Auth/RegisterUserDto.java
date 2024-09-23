package com.back_end.forum.dto.Auth;

import lombok.Data;

@Data
public class RegisterUserDto {

    private String email;
    private String username;
    private String password;
}
