package com.SuperForum.forum_app.model;

import jakarta.persistence.*;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "Role")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roleId;

    @Column(length = 24, unique = true, nullable = false)
    private String role_name;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "roles_permissions",
            joinColumns = @JoinColumn(name = "roles_role_id"),
            inverseJoinColumns = @JoinColumn(name = "permissions_permission_id"))
    private Set<Permission> permissions;

}
