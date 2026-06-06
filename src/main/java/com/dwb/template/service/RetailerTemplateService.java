package com.dwb.template.service;

import com.dwb.common.dto.BaseResponse;
import com.dwb.template.dto.TemplateRequest;
import com.dwb.template.dto.TemplateResponse;
import org.springframework.web.multipart.MultipartFile;

public interface RetailerTemplateService {

    BaseResponse<TemplateResponse> getTemplate();

    BaseResponse<TemplateResponse> saveTemplate(TemplateRequest request, MultipartFile storeLogo);
}
