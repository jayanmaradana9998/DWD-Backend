package com.dwb.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AdminDashboardResponse {

    private long totalUsers;
    private long activeUsers;
    private long pendingUsers;         // status = PENDING (not fully verified yet)
    private long blockedUsers;
    private long totalRetailers;
    private long pendingKycCount;
    private long approvedKycCount;
    private long rejectedKycCount;
}
