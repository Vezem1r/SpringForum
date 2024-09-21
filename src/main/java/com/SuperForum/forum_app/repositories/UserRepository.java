package com.SuperForum.forum_app.repositories;

import com.SuperForum.forum_app.entities.User;
import com.SuperForum.forum_app.enums.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    User findByUserRole (UserRole userRole);
}
