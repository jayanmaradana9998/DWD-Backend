package com.dwb.kyc.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class KycStatusResponse {

    private String status;        // "NOT_SUBMITTED", "PENDING", "APPROVED", "REJECTED"
    private String idType;
    private String fullName;
    private LocalDateTime submittedAt;
}
