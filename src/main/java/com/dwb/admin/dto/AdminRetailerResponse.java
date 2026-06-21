package com.dwb.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class AdminRetailerResponse {

    private Long retailerId;
    private String retailerUniqueId;
    private String storeName;
    private String storeType;
    private String ownerName;
    private String email;
    private String phoneNumber;
    private String gst;
    private String pan;
    private String city;
    private String state;
    private String kycStatus;        // pulled from latest KycDocument for this user
    private LocalDateTime registeredAt;
}
