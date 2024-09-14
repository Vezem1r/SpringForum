package com.SuperForum.forum_app.model;

import jakarta.persistence.*;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "User")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(length = 63, nullable = false, unique = true)
    private String username;

    @Column(length = 127, nullable = false, unique = true)
    private String email;

    @Column(length = 127, nullable = false)
    private String password;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_user_id"),
            inverseJoinColumns = @JoinColumn(name = "roles_role_id")
    )
    private Set<Role> roles;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
