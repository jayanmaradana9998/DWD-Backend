package com.dwb.warranty.dto;

import com.dwb.warranty.entity.WarrantyType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class WarrantyResponse {

    private Long id;
    private String warrantyNumber;
    private Long invoiceId;
    private String invoiceNumber;
    private Long customerId;
    private String customerName;
    private WarrantyType warrantyType;
    private String provider;
    private Integer durationMonths;
    private LocalDate startDate;
    private LocalDate endDate;
    private String coverageSummary;
    private Boolean isExtended;
    private Boolean skipped;
    private LocalDateTime createdAt;
}
