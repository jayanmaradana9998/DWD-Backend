package com.dwb.auth.service;

import com.dwb.auth.dto.RegisterRequest;
import com.dwb.common.dto.BaseResponse;

public interface AuthService {
    BaseResponse<Object> register(RegisterRequest request);
}