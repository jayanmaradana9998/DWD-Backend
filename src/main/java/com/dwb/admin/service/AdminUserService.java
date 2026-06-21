package com.dwb.admin.service;

import com.dwb.admin.dto.AdminRetailerResponse;
import com.dwb.admin.dto.AdminUserResponse;
import com.dwb.common.dto.BaseResponse;

import java.util.List;

public interface AdminUserService {

    BaseResponse<List<AdminUserResponse>> getAllUsers();

    BaseResponse<Object> blockUser(Long userId);

    BaseResponse<Object> unblockUser(Long userId);

    BaseResponse<List<AdminRetailerResponse>> getAllRetailers();
}
