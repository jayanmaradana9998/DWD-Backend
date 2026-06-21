package com.dwb.auth.password.controller;

import com.dwb.auth.password.dto.ForgotPasswordRequest;
import com.dwb.auth.password.dto.ResetPasswordRequest;
import com.dwb.auth.password.dto.SetPasswordRequest;
import com.dwb.auth.password.service.PasswordService;
import com.dwb.common.dto.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Auth - Password", description = "Password setup, forgot and reset flows")
public class PasswordController {

    private final PasswordService passwordService;

    // Called after first OTP login when passwordSet = false
    @PostMapping("/set-password")
    @Operation(summary = "Set password for first-time login (requires JWT token)")
    public ResponseEntity<BaseResponse<Void>> setPassword(@Valid @RequestBody SetPasswordRequest request) {
        passwordService.setPassword(request);
        return ResponseEntity.ok(new BaseResponse<>(true, "Password set successfully", null));
    }

    // Public — no token needed
    @PostMapping("/forgot-password")
    @Operation(summary = "Send password reset OTP to email")
    public ResponseEntity<BaseResponse<Void>> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        passwordService.forgotPassword(request);
        return ResponseEntity.ok(new BaseResponse<>(true, "Password reset OTP sent to your email", null));
    }

    // Public — no token needed
    @PostMapping("/reset-password")
    @Operation(summary = "Reset password using email OTP")
    public ResponseEntity<BaseResponse<Void>> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        passwordService.resetPassword(request);
        return ResponseEntity.ok(new BaseResponse<>(true, "Password reset successfully", null));
    }
}
