package com.dwb.auth.login.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Set;

@Getter
@AllArgsConstructor
public class LoginResponse {

    private String token;
    private String uniqueId;
    private Set<String> roles;
}
