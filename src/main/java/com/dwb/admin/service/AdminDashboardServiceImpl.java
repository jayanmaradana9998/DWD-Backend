package com.dwb.admin.service;

import com.dwb.admin.dto.AdminDashboardResponse;
import com.dwb.common.dto.BaseResponse;
import com.dwb.kyc.entity.KycStatus;
import com.dwb.kyc.repository.KycDocumentRepository;
import com.dwb.retailer.repository.RetailerProfileRepository;
import com.dwb.user.entity.UserStatus;
import com.dwb.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminDashboardServiceImpl implements AdminDashboardService {

    private final UserRepository userRepository;
    private final RetailerProfileRepository retailerProfileRepository;
    private final KycDocumentRepository kycDocumentRepository;

    @Override
    public BaseResponse<AdminDashboardResponse> getDashboard() {

        long totalUsers    = userRepository.count();
        long activeUsers   = userRepository.countByStatus(UserStatus.ACTIVE);
        long pendingUsers  = userRepository.countByStatus(UserStatus.PENDING);
        long blockedUsers  = userRepository.countByStatus(UserStatus.BLOCKED);
        long totalRetailers = retailerProfileRepository.count();
        long pendingKyc   = kycDocumentRepository.countByStatus(KycStatus.PENDING);
        long approvedKyc  = kycDocumentRepository.countByStatus(KycStatus.APPROVED);
        long rejectedKyc  = kycDocumentRepository.countByStatus(KycStatus.REJECTED);

        return new BaseResponse<>(true, "Dashboard fetched", new AdminDashboardResponse(
                totalUsers, activeUsers, pendingUsers, blockedUsers,
                totalRetailers, pendingKyc, approvedKyc, rejectedKyc
        ));
    }
}
