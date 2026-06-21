package com.dwb.template.entity;

import com.dwb.common.entity.BaseEntity;
import com.dwb.retailer.entity.RetailerProfile;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "retailer_template_settings")
public class RetailerTemplateSettings extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "retailer_profile_id", nullable = false, unique = true)
    private RetailerProfile retailerProfile;

    @Column(nullable = false)
    private String invoiceTemplateType;

    @Column(nullable = false)
    private String warrantyTemplateType;

    private String storeLogoPath;

    private String primaryColor;

    @Column(columnDefinition = "TEXT")
    private String footerText;
}
