package com.dwb.auth.controller;

import com.dwb.auth.dto.LoginRequest;
import com.dwb.auth.dto.LoginResponse;
import com.dwb.auth.service.LoginService;
import com.dwb.common.dto.BaseResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class LoginController {

    private final LoginService loginService;

    @PostMapping("/login")
    public ResponseEntity<BaseResponse<LoginResponse>> login(
            @Valid @RequestBody LoginRequest request) {

        LoginResponse response = loginService.login(request);

        return ResponseEntity.ok(
                new BaseResponse<>(
                        true,
                        "Login successful",
                        response
                )
        );
    }
}