package com.back_end.forum.model.enums;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.back_end.forum.model.enums.Permission.*;


@RequiredArgsConstructor
public enum RolesEnum {
    ADMIN(
            Set.of(
                    ADMIN_READ,
                    ADMIN_CREATE,
                    ADMIN_UPDATE,
                    ADMIN_DELETE,
                    MODERATOR_READ,
                    MODERATOR_CREATE,
                    MODERATOR_UPDATE,
                    MODERATOR_DELETE
            )
    ),
    MODERATOR(
            Set.of(
                    MODERATOR_READ,
                    MODERATOR_CREATE,
                    MODERATOR_UPDATE,
                    MODERATOR_DELETE
            )
    ),
    USER(Collections.emptySet())
    ;

    @Getter
    private final Set<Permission> persmission;

    public List<SimpleGrantedAuthority> getAuthorities(){
        var authorities = getPersmission().stream()
                .map(permission -> new SimpleGrantedAuthority(permission.name()))
                .toList();
        authorities.add(new SimpleGrantedAuthority("ROLE_" +this.name()));
        return authorities;
    }
}
