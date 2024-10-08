package com.back_end.forum.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Entity
@Data
@Table(name = "users")
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(length = 63, nullable = false, unique = true)
    private String username;

    @Column(length = 127, nullable = false, unique = true)
    private String email;

    @Column(length = 127, nullable = false)
    @JsonIgnore
    private String password;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime lastLogin;

    @Column
    private String profilePicture;

    /*-----------------------------------------------------*/

    @JsonIgnore
    @Column(name = "verification_code")
    private String verificationCode;

    @JsonIgnore
    @Column(name = "verification_expiration")
    private LocalDateTime verificationCodeExpiredAt;

    @JsonIgnore
    private boolean enabled;

    /*-----------------------------------------------------*/

    @JsonIgnore
    @Column(name = "password_reset_code")
    private String passwordResetCode;

    @JsonIgnore
    @Column(name = "password_reset_expiration")
    private LocalDateTime passwordResetCodeExpiredAt;

    /*-----------------------------------------------------*/

    @JsonIgnore
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isEnabled() {
        return enabled;
    }
}
