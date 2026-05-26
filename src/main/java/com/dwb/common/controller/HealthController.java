package com.dwb.common.controller;

import com.dwb.common.dto.BaseResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
public class HealthController {

    @GetMapping("/health")
    public BaseResponse<Map<String, Object>> health() {

        Map<String, Object> response = Map.of(
                "status", "UP",
                "service", "DWB Backend",
                "timestamp", LocalDateTime.now()
        );

        return new BaseResponse<>(true, "Service is running", response);
    }
}