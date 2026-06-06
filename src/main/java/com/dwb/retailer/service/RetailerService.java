package com.dwb.retailer.service;

import com.dwb.common.dto.BaseResponse;
import com.dwb.retailer.dto.RetailerRegisterRequest;
import com.dwb.retailer.dto.RetailerRegisterResponse;

public interface RetailerService {

    BaseResponse<RetailerRegisterResponse> register(RetailerRegisterRequest request);

    BaseResponse<RetailerRegisterResponse> getProfile();
}
