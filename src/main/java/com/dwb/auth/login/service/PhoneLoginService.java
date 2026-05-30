package com.dwb.auth.login.service;

import com.dwb.auth.login.dto.LoginResponse;
import com.dwb.auth.login.dto.SendPhoneLoginOtpRequest;
import com.dwb.auth.login.dto.VerifyPhoneLoginOtpRequest;
import com.dwb.common.dto.BaseResponse;

public interface PhoneLoginService {

    BaseResponse<Object> sendPhoneLoginOtp(SendPhoneLoginOtpRequest request);

    BaseResponse<LoginResponse> verifyPhoneLoginOtp(VerifyPhoneLoginOtpRequest request);
}
