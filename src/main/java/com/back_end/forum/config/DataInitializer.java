package com.back_end.forum.config;

import com.back_end.forum.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserService userService;


    @Override
    public void run(String... args) throws Exception {

        userService.createGuest();
        userService.createAdmin();

    }
}
