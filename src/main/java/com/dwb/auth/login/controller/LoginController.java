package com.dwb.auth.login.controller;

import com.dwb.auth.login.dto.LoginRequest;
import com.dwb.auth.login.dto.LoginResponse;
import com.dwb.auth.login.dto.SendPhoneLoginOtpRequest;
import com.dwb.auth.login.dto.VerifyPhoneLoginOtpRequest;
import com.dwb.auth.login.service.LoginService;
import com.dwb.auth.login.service.PhoneLoginService;
import com.dwb.common.dto.BaseResponse;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class LoginController {

    private final LoginService loginService;
    private final PhoneLoginService phoneLoginService;

    // Email + password login
    @PostMapping("/login")
    @Operation(summary = "Login By Email and Password")
    public BaseResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = loginService.login(request);
        return new BaseResponse<>(true, "Login successful", response);
    }

    // Phone login — step 1: send OTP
    @PostMapping("/send-phone-login-otp")
    public BaseResponse<Object> sendPhoneLoginOtp(@Valid @RequestBody SendPhoneLoginOtpRequest request) {
        return phoneLoginService.sendPhoneLoginOtp(request);
    }

    // Phone login — step 2: verify OTP → get token
    @PostMapping("/verify-phone-login-otp")
    @Operation(summary ="Login By Phone OTP")
    public BaseResponse<LoginResponse> verifyPhoneLoginOtp(@Valid @RequestBody VerifyPhoneLoginOtpRequest request) {
        return phoneLoginService.verifyPhoneLoginOtp(request);
    }
}
