package com.SuperForum.forum_app.services.auth;

import com.SuperForum.forum_app.dtos.SignupRequest;
import com.SuperForum.forum_app.dtos.UserDto;

public interface AuthService {
    UserDto createUser(SignupRequest signupRequest);
}
