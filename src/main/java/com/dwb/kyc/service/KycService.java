package com.dwb.kyc.service;

import com.dwb.common.dto.BaseResponse;
import com.dwb.kyc.dto.KycStatusResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface KycService {

    BaseResponse<Object> submitKyc(
            String fullName,
            String idType,
            String idNumber,
            String gstNumber,
            List<MultipartFile> documents
    );

    BaseResponse<KycStatusResponse> getStatus();
}
