package com.back_end.forum.responses;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponse {
    private String token;
    private long expiredIn;
    private String message;

    public LoginResponse(String token, String message) {
        this.token = token;
        this.expiredIn = 0;
        this.message = message;
    }
}
