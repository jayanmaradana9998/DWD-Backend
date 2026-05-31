package com.dwb.otp.repository;

import com.dwb.otp.entity.PhoneOtp;
import com.dwb.otp.entity.PhoneOtpType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PhoneOtpRepository extends JpaRepository<PhoneOtp, Long> {

    Optional<PhoneOtp> findTopByUser_PhoneNumberAndVerifiedFalseAndTypeOrderByCreatedAtDesc(
            String phoneNumber, PhoneOtpType type
    );
}
