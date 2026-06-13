package com.dwb.customer.entity;

import com.dwb.common.entity.BaseEntity;
import com.dwb.retailer.entity.RetailerProfile;
import com.dwb.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "customers")
public class Customer extends BaseEntity {

    // Linked User account — created automatically when retailer adds a customer
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true)
    private User user;

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
