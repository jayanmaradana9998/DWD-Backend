package com.dwb.retailer.entity;

import com.dwb.common.entity.BaseEntity;
import com.dwb.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "retailer_profiles")
public class RetailerProfile extends BaseEntity {

    // One user can have one retailer profile
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    // Generated after save: RET000001, RET000002, etc.
    @Column(unique = true)
    private String retailerUniqueId;

    @Column(nullable = false)
    private String storeName;

    @Column(nullable = false)
    private String storeType;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private String state;

    @Column(nullable = false)
    private String pincode;

    @Column(nullable = false)
    private String gst;

    @Column(nullable = false)
    private String pan;

    // Optional: Mon-Fri 9AM-6PM
    private String operatingHours;
}
