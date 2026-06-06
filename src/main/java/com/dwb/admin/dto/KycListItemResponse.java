package com.dwb.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class KycListItemResponse {

    private Long kycId;
    private Long userId;
    private String userUniqueId;
    private String fullName;
    private String idType;
    private String status;
    private LocalDateTime submittedAt;
}
