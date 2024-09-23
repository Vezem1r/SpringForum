package com.back_end.forum.dto.Auth;

import lombok.Data;

@Data
public class LoginUserDto {
    private String usernameOrEmail;
    private String password;
}
