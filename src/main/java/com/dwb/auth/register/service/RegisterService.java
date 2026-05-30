package com.dwb.auth.register.service;

import com.dwb.auth.register.dto.RegisterRequest;
import com.dwb.auth.register.dto.VerifyEmailOtpRequest;
import com.dwb.common.dto.BaseResponse;

public interface RegisterService {

    BaseResponse<Object> register(RegisterRequest request);

    BaseResponse<Object> verifyEmailOtp(VerifyEmailOtpRequest request);
}
