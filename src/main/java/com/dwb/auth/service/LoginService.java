package com.dwb.auth.service;

import com.dwb.auth.dto.LoginRequest;
import com.dwb.auth.dto.LoginResponse;

public interface LoginService {

    LoginResponse login(LoginRequest request);
}