package com.dwb.auth.controller;

import com.dwb.auth.dto.RegisterRequest;
import com.dwb.auth.dto.VerifyEmailOtpRequest;
import com.dwb.auth.service.AuthService;
import com.dwb.common.dto.BaseResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public BaseResponse<Object> register(@Valid @RequestBody RegisterRequest request) {
        return authService.register(request);
    }       
    

    @PostMapping("/verify-email-otp")
    public BaseResponse<Object> verifyEmailOtp(@Valid @RequestBody VerifyEmailOtpRequest request) {
        return authService.verifyEmailOtp(request);
    }
}