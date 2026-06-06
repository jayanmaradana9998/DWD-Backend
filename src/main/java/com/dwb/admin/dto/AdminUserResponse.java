package com.dwb.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@AllArgsConstructor
public class AdminUserResponse {

    private Long id;
    private String uniqueId;
    private String fullName;
    private String email;
    private String phoneNumber;
    private Set<String> roles;
    private String status;
    private Boolean emailVerified;
    private Boolean phoneVerified;
    private LocalDateTime createdAt;
}
