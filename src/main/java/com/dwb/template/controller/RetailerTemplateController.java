package com.dwb.template.controller;

import com.dwb.common.dto.BaseResponse;
import com.dwb.template.dto.TemplateRequest;
import com.dwb.template.dto.TemplateResponse;
import com.dwb.template.service.RetailerTemplateService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/retailer/templates")
@RequiredArgsConstructor
public class RetailerTemplateController {

    private final RetailerTemplateService templateService;

    @GetMapping
    public BaseResponse<TemplateResponse> getTemplate() {
        return templateService.getTemplate();
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public BaseResponse<TemplateResponse> saveTemplate(
            @Valid @ModelAttribute TemplateRequest request,
            @RequestPart(value = "storeLogo", required = false) MultipartFile storeLogo) {
        return templateService.saveTemplate(request, storeLogo);
    }
}
