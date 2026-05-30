package com.dwb.otp.controller;

import com.dwb.common.dto.BaseResponse;
import com.dwb.otp.dto.SendPhoneOtpRequest;
import com.dwb.otp.dto.VerifyPhoneOtpRequest;
import com.dwb.otp.service.OtpService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/otp")
@RequiredArgsConstructor
public class OtpController {

    private final OtpService otpService;

    @PostMapping("/send-phone-otp")
    public BaseResponse<Object> sendPhoneOtp(@Valid @RequestBody SendPhoneOtpRequest request) {
        return otpService.sendPhoneOtp(request);
    }

    @PostMapping("/verify-phone-otp")
    public BaseResponse<Object> verifyPhoneOtp(@Valid @RequestBody VerifyPhoneOtpRequest request) {
        return otpService.verifyPhoneOtp(request);
    }
}
