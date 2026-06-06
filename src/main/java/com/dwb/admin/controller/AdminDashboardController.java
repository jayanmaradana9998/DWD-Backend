package com.dwb.admin.controller;

import com.dwb.admin.dto.AdminDashboardResponse;
import com.dwb.admin.service.AdminDashboardService;
import com.dwb.common.dto.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@Tag(name = "Admin - Dashboard", description = "Admin dashboard with platform-wide counts and stats")
public class AdminDashboardController {

    private final AdminDashboardService adminDashboardService;

    @GetMapping("/dashboard")
    @Operation(summary = "Get admin dashboard stats", description = "Returns total users, active/pending/blocked counts, total retailers, and KYC submission counts by status")
    public BaseResponse<AdminDashboardResponse> getDashboard() {
        return adminDashboardService.getDashboard();
    }
}
