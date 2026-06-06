package com.dwb.customer.entity;

import com.dwb.common.entity.BaseEntity;
import com.dwb.retailer.entity.RetailerProfile;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "customers")
public class Customer extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "retailer_profile_id", nullable = false)
    private RetailerProfile retailerProfile;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String phone;

    // Optional
    private String email;

    // Generated after save: RCUS000001, RCUS000002, etc.
    @Column(unique = true)
    private String uniqueId;

    @Column(nullable = false)
    private Boolean phoneVerified = false;
}
