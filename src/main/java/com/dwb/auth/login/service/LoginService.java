package com.dwb.auth.login.service;

import com.dwb.auth.login.dto.LoginRequest;
import com.dwb.auth.login.dto.LoginResponse;

public interface LoginService {

    LoginResponse login(LoginRequest request);
}
