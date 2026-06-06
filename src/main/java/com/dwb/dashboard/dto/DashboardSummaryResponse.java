package com.dwb.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class DashboardSummaryResponse {

    private long totalCustomers;
    private long totalInvoices;
    private long totalWarranties;
    private List<RecentTransactionDto> recentTransactions;

    @Getter
    @Setter
    @AllArgsConstructor
    public static class RecentTransactionDto {
        private String invoiceNumber;
        private String customerName;
        private String totalAmount;
        private String createdAt;
    }
}
