package com.dwb.admin.service;

import com.dwb.admin.dto.KycDetailResponse;
import com.dwb.admin.dto.KycListItemResponse;
import com.dwb.common.dto.BaseResponse;

import java.util.List;

public interface AdminKycService {

    BaseResponse<List<KycListItemResponse>> getAllKyc(String status);

    BaseResponse<KycDetailResponse> getKycDetail(Long kycId);

    BaseResponse<Object> approveKyc(Long kycId);

    BaseResponse<Object> rejectKyc(Long kycId, String reason);
}
