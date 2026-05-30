package com.dwb.otp.entity;

import com.dwb.common.entity.BaseEntity;
import com.dwb.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "phone_otps")
public class PhoneOtp extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String otp;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    @Column(nullable = false)
    private Boolean verified = false;

    @Column(nullable = false)
    private Integer attempts = 0;

    // Differentiates between registration verification and phone login OTP
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PhoneOtpType type;
}
