package com.dwb.auth.register.controller;

import com.dwb.auth.register.dto.RegisterRequest;
import com.dwb.auth.register.dto.VerifyEmailOtpRequest;
import com.dwb.auth.register.service.RegisterService;
import com.dwb.common.dto.BaseResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class RegisterController {

    private final RegisterService registerService;

    @PostMapping("/register")
    public BaseResponse<Object> register(@Valid @RequestBody RegisterRequest request) {
        return registerService.register(request);
    }

    @PostMapping("/verify-email-otp")
    public BaseResponse<Object> verifyEmailOtp(@Valid @RequestBody VerifyEmailOtpRequest request) {
        return registerService.verifyEmailOtp(request);
    }
}
