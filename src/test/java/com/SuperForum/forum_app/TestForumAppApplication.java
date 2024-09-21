package com.SuperForum.forum_app;

import org.springframework.boot.SpringApplication;

public class TestForumAppApplication {

	public static void main(String[] args) {
		SpringApplication.from(ForumAppApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
