package com.dwb.warranty.entity;

import com.dwb.common.entity.BaseEntity;
import com.dwb.customer.entity.Customer;
import com.dwb.invoice.entity.Invoice;
import com.dwb.retailer.entity.RetailerProfile;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "warranties")
public class Warranty extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id", nullable = false)
    private Invoice invoice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "retailer_profile_id", nullable = false)
    private RetailerProfile retailerProfile;

    @Column(unique = true)
    private String warrantyNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WarrantyType warrantyType;

    private String provider;

    @Column(nullable = false)
    private Integer durationMonths;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @Column(columnDefinition = "TEXT")
    private String coverageSummary;

    // true = this is an extended warranty on top of existing manufacturer warranty
    @Column(nullable = false)
    private Boolean isExtended = false;

    // true = retailer chose to skip warranty for this invoice
    @Column(nullable = false)
    private Boolean skipped = false;
}
