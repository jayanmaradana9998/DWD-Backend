package com.dwb.admin.service;

import com.dwb.admin.dto.AdminDashboardResponse;
import com.dwb.common.dto.BaseResponse;

public interface AdminDashboardService {

    BaseResponse<AdminDashboardResponse> getDashboard();
}
