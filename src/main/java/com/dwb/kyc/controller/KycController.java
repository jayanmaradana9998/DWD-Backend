package com.dwb.kyc.controller;

import com.dwb.common.dto.BaseResponse;
import com.dwb.kyc.dto.KycStatusResponse;
import com.dwb.kyc.service.KycService;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/kyc")
@RequiredArgsConstructor
public class KycController {

    private final KycService kycService;

    // consumes MULTIPART_FORM_DATA because we receive both text fields and files in one request
    @PostMapping(value = "/submit", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public BaseResponse<Object> submitKyc(
            @RequestParam @NotBlank(message = "Full name is required") String fullName,
            @RequestParam @NotBlank(message = "ID type is required") String idType,
            @RequestParam @NotBlank(message = "ID number is required") String idNumber,
            @RequestParam @NotBlank(message = "GST number is required") String gstNumber,
            @RequestParam("documents") List<MultipartFile> documents
    ) {
        return kycService.submitKyc(fullName, idType, idNumber, gstNumber, documents);
    }

    @GetMapping("/status")
    public BaseResponse<KycStatusResponse> getStatus() {
        return kycService.getStatus();
    }
}
