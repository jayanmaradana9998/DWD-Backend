package com.dwb.user.entity;

import com.dwb.common.entity.BaseEntity;
import com.dwb.role.entity.Role;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "users")
public class User extends BaseEntity {

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, unique = true)
    private String phoneNumber;

    @Column(nullable = false)
    private String password;

    // Stored in a separate user_roles table: user_id | role
    // EAGER means roles are loaded together with the user (no extra DB call)
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id")
    )
    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private Set<Role> roles = new HashSet<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatus status;

    @Column(nullable = false)
    private Boolean emailVerified = false;

    @Column(nullable = false)
    private Boolean phoneNumberVerified = false;

    // Generated as USR000001 only after BOTH email + phone are verified
    @Column(unique = true)
    private String uniqueId;
}
