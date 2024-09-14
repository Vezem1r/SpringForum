package com.SuperForum.forum_app.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "Permission")
public class Permission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long permissionId;

    @Column(length = 64, nullable = false, unique = true)
    private String permission_name;
}
