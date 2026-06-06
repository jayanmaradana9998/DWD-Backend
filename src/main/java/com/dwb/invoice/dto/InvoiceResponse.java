package com.dwb.invoice.dto;

import com.dwb.invoice.entity.InvoiceStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class InvoiceResponse {

    private Long id;
    private String invoiceNumber;
    private Long customerId;
    private String customerName;
    private String customerPhone;
    private BigDecimal subtotal;
    private BigDecimal taxTotal;
    private BigDecimal platformCharge;
    private BigDecimal warrantyCharge;
    private BigDecimal totalAmount;
    private InvoiceStatus status;
    private List<InvoiceItemResponse> items;
    private LocalDateTime createdAt;
}
