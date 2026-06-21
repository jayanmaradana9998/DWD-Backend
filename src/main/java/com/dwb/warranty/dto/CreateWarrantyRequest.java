package com.dwb.warranty.dto;

import com.dwb.warranty.entity.WarrantyType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateWarrantyRequest {

    @NotNull(message = "Invoice ID is required")
    private Long invoiceId;

    // If true, saves a skipped warranty record — no other fields required
    private boolean skipped = false;

    private WarrantyType warrantyType;

    private String provider;

    @Min(value = 1, message = "Duration must be at least 1 month")
    private Integer durationMonths;

    private String coverageSummary;

    private Boolean isExtended = false;
}
