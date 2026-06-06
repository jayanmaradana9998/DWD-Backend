package com.dwb.admin.controller;

import com.dwb.admin.dto.AdminRetailerResponse;
import com.dwb.admin.dto.AdminUserResponse;
import com.dwb.admin.service.AdminUserService;
import com.dwb.common.dto.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@Tag(name = "Admin - User Management", description = "Admin endpoints for managing users and retailers")
public class AdminUserController {

    private final AdminUserService adminUserService;

    @GetMapping("/users")
    @Operation(summary = "List all users", description = "Returns every registered user with their roles, status, and verification state")
    public BaseResponse<List<AdminUserResponse>> getAllUsers() {
        return adminUserService.getAllUsers();
    }

    @PostMapping("/users/{userId}/block")
    @Operation(summary = "Block a user", description = "Sets user status to BLOCKED. User cannot login until unblocked.")
    public BaseResponse<Object> blockUser(@PathVariable Long userId) {
        return adminUserService.blockUser(userId);
    }

    @PostMapping("/users/{userId}/unblock")
    @Operation(summary = "Unblock a user", description = "Restores user status to ACTIVE.")
    public BaseResponse<Object> unblockUser(@PathVariable Long userId) {
        return adminUserService.unblockUser(userId);
    }

    @GetMapping("/retailers")
    @Operation(summary = "List all retailers", description = "Returns all retailer profiles with store info, owner details, and their current KYC status")
    public BaseResponse<List<AdminRetailerResponse>> getAllRetailers() {
        return adminUserService.getAllRetailers();
    }
}
