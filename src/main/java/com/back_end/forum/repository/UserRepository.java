package com.back_end.forum.repository;

import com.back_end.forum.model.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {
    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);

    Boolean existsByUsername(String username);

    Optional<User> findByPasswordResetCode(String passwordResetCode);

    boolean existsByPasswordResetCode(String passwordResetCode);

    @Query("SELECT COUNT(u) FROM User u WHERE u.lastLogin >= :todayStart")
    long countUsersByLastLogin(@Param("todayStart") LocalDateTime todayStart);

}
