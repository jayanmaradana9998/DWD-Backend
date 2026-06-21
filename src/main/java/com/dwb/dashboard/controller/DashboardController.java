package com.dwb.dashboard.controller;

import com.dwb.common.dto.BaseResponse;
import com.dwb.customer.repository.CustomerRepository;
import com.dwb.dashboard.dto.DashboardSummaryResponse;
import com.dwb.exception.custom.BadRequestException;
import com.dwb.invoice.entity.Invoice;
import com.dwb.invoice.repository.InvoiceRepository;
import com.dwb.retailer.entity.RetailerProfile;
import com.dwb.retailer.repository.RetailerProfileRepository;
import com.dwb.security.util.SecurityUtils;
import com.dwb.user.repository.UserRepository;
import com.dwb.warranty.repository.WarrantyRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
@Tag(name = "Dashboard", description = "Retailer dashboard summary")
public class DashboardController {

    private final CustomerRepository customerRepository;
    private final InvoiceRepository invoiceRepository;
    private final WarrantyRepository warrantyRepository;
    private final RetailerProfileRepository retailerProfileRepository;
    private final UserRepository userRepository;

    @GetMapping("/summary")
    @Operation(summary = "Get dashboard summary — counts + 5 recent transactions")
    public ResponseEntity<BaseResponse<DashboardSummaryResponse>> getSummary() {
        RetailerProfile retailer = getRetailerProfile();
        Long rid = retailer.getId();

        long totalCustomers = customerRepository.countByRetailerProfile_Id(rid);
        long totalInvoices = invoiceRepository.countByRetailerProfile_Id(rid);
        long totalWarranties = warrantyRepository.countByRetailerProfile_Id(rid);

        List<Invoice> recent = invoiceRepository
                .findByRetailerProfile_IdOrderByCreatedAtDesc(rid)
                .stream().limit(5).collect(Collectors.toList());

        List<DashboardSummaryResponse.RecentTransactionDto> recentTransactions = recent.stream().map(inv ->
                new DashboardSummaryResponse.RecentTransactionDto(
                        inv.getInvoiceNumber(),
                        inv.getCustomer().getName(),
                        "₹" + inv.getTotalAmount().toPlainString(),
                        inv.getCreatedAt().toString()
                )
        ).collect(Collectors.toList());

        DashboardSummaryResponse summary = new DashboardSummaryResponse(
                totalCustomers, totalInvoices, totalWarranties, recentTransactions);

        return ResponseEntity.ok(new BaseResponse<>(true, "Dashboard summary fetched", summary));
    }

    private RetailerProfile getRetailerProfile() {
        String email = SecurityUtils.getCurrentUserEmail();
        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadRequestException("User not found"));
        return retailerProfileRepository.findByUser_Id(user.getId())
                .orElseThrow(() -> new BadRequestException("Retailer profile not found"));
    }
}
