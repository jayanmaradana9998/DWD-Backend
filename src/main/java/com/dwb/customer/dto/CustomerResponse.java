package com.dwb.customer.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class CustomerResponse {

    private Long id;
    private String uniqueId;
    private String name;
    private String phone;
    private String email;
    private Boolean phoneVerified;
    private LocalDateTime createdAt;
}
