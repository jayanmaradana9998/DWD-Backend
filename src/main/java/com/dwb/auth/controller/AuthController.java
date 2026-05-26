package com.dwb.auth.controller;

import com.dwb.auth.dto.RegisterRequest;
import com.dwb.common.dto.BaseResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @PostMapping("/register")
    public BaseResponse<Object> register(
            @Valid @RequestBody RegisterRequest request
    ) {

        return new BaseResponse<>(
                true,
                "Registration request received",
                request
        );
    }
}