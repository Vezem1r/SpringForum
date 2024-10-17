package com.back_end.forum.config;

import com.back_end.forum.service.CategoryService;
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
    private final CategoryService categoryService;


    @Override
    public void run(String... args) throws Exception {

        //Creating Admin, Guest and Users accounts
        userService.createGuest();
        userService.createAdmin();
        userService.createUsers(10);

        //Creating Categories
        categoryService.createCategories(10);

    }
}
