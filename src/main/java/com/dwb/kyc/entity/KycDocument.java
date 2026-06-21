package com.dwb.kyc.entity;

import com.dwb.common.entity.BaseEntity;
import com.dwb.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "kyc_documents")
public class KycDocument extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String fullName;

    // e.g. "Aadhaar Card", "PAN Card", "Passport", "Driving License", "Voter ID"
    @Column(nullable = false)
    private String idType;

    @Column(nullable = false)
    private String idNumber;

    @Column(nullable = false)
    private String gstNumber;

    // Stores file paths for each uploaded document
    // e.g. ["kyc/1/abc123.pdf", "kyc/1/def456.jpg"]
    @ElementCollection
    @CollectionTable(name = "kyc_document_files", joinColumns = @JoinColumn(name = "kyc_document_id"))
    @Column(name = "file_path")
    private List<String> documentPaths = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private KycStatus status = KycStatus.PENDING;

    // Filled by admin when rejecting — so retailer knows why it was rejected
    @Column(columnDefinition = "TEXT")
    private String rejectionReason;
}
