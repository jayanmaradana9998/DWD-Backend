package com.dwb.retailer.controller;

import com.dwb.common.dto.BaseResponse;
import com.dwb.retailer.dto.RetailerRegisterRequest;
import com.dwb.retailer.dto.RetailerRegisterResponse;
import com.dwb.retailer.service.RetailerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/retailer")
@RequiredArgsConstructor
public class RetailerController {

    private final RetailerService retailerService;

    @PostMapping("/register")
    public BaseResponse<RetailerRegisterResponse> register(
            @Valid @RequestBody RetailerRegisterRequest request) {
        return retailerService.register(request);
    }

    @GetMapping("/profile")
    public BaseResponse<RetailerRegisterResponse> getProfile() {
        return retailerService.getProfile();
    }
}
