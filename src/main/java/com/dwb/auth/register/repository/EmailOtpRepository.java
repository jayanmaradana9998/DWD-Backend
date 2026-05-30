package com.dwb.auth.register.repository;

import com.dwb.auth.register.entity.EmailOtp;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmailOtpRepository extends JpaRepository<EmailOtp, Long> {

    Optional<EmailOtp> findTopByUser_EmailAndVerifiedFalseOrderByCreatedAtDesc(String email);
}
