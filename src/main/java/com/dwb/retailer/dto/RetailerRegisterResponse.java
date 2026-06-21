package com.dwb.retailer.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RetailerRegisterResponse {

    private Long retailerId;
    private String retailerUniqueId;   // RET000001
    private String storeName;
    private String ownerName;          // from User.fullName
    private String email;              // from User.email
    private String phoneNumber;        // from User.phoneNumber
}
