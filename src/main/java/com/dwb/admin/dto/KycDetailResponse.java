package com.dwb.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class KycDetailResponse {

    private Long kycId;
    private Long userId;
    private String userUniqueId;
    private String userEmail;
    private String userPhone;
    private String fullName;
    private String idType;
    private String idNumber;
    private String gstNumber;
    private List<String> documentPaths;   // file paths — use file serve endpoint to view
    private String status;
    private String rejectionReason;
    private LocalDateTime submittedAt;
}
