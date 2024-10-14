package com.back_end.forum.model.enums;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.back_end.forum.model.enums.Permission.*;


@RequiredArgsConstructor
@Slf4j
public enum RolesEnum {
    ADMIN(
            Set.of(
                    ADMIN_READ,
                    ADMIN_CREATE,
                    ADMIN_UPDATE,
                    ADMIN_DELETE,
                    USER_READ,
                    USER_CREATE,
                    USER_UPDATE,
                    USER_DELETE
            )
    ),
    USER(
            Set.of(
                    USER_READ,
                    USER_CREATE,
                    USER_UPDATE,
                    USER_DELETE
            )
    ),
    GUEST(Collections.emptySet())
    ;

    @Getter
    private final Set<Permission> persmission;

    public List<SimpleGrantedAuthority> getAuthorities(){
        var authorities = new java.util.ArrayList<>(getPersmission().stream()
                .map(permission -> new SimpleGrantedAuthority(permission.name()))
                .toList());
        authorities.add(new SimpleGrantedAuthority("ROLE_" +this.name()));
        log.info("Authorities: {}", authorities);
        return authorities;
    }
}
