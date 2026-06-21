package com.dwb.customer.repository;

import com.dwb.customer.entity.CustomerOtp;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerOtpRepository extends JpaRepository<CustomerOtp, Long> {

    // Latest unverified OTP for a customer — used for verification
    Optional<CustomerOtp> findTopByCustomer_IdAndVerifiedFalseOrderByCreatedAtDesc(Long customerId);
}
