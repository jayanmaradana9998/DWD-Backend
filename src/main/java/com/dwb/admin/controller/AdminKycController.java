package com.dwb.admin.controller;

import com.dwb.admin.dto.KycDetailResponse;
import com.dwb.admin.dto.KycListItemResponse;
import com.dwb.admin.dto.KycRejectRequest;
import com.dwb.admin.service.AdminKycService;
import com.dwb.common.dto.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/kyc")
@RequiredArgsConstructor
@Tag(name = "Admin - KYC Management", description = "Admin endpoints for reviewing and actioning KYC submissions")
public class AdminKycController {

    private final AdminKycService adminKycService;

    @GetMapping
    @Operation(summary = "List all KYC submissions", description = "Returns all KYC records. Filter by status using ?status=PENDING, APPROVED, or REJECTED")
    public BaseResponse<List<KycListItemResponse>> getAllKyc(
            @RequestParam(required = false) String status) {
        return adminKycService.getAllKyc(status);
    }

    @GetMapping("/{kycId}")
    @Operation(summary = "Get KYC detail", description = "Returns full KYC info including document file paths for a single submission")
    public BaseResponse<KycDetailResponse> getKycDetail(@PathVariable Long kycId) {
        return adminKycService.getKycDetail(kycId);
    }

    @PostMapping("/{kycId}/approve")
    @Operation(summary = "Approve KYC", description = "Sets KYC status to APPROVED. Retailer will see approved status immediately.")
    public BaseResponse<Object> approveKyc(@PathVariable Long kycId) {
        return adminKycService.approveKyc(kycId);
    }

    @PostMapping("/{kycId}/reject")
    @Operation(summary = "Reject KYC", description = "Sets KYC status to REJECTED with a reason. Retailer can see the reason and resubmit.")
    public BaseResponse<Object> rejectKyc(
            @PathVariable Long kycId,
            @Valid @RequestBody KycRejectRequest request) {
        return adminKycService.rejectKyc(kycId, request.getReason());
    }
}
