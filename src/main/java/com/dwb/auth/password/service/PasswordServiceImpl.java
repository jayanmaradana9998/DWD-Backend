package com.dwb.auth.password.service;

import com.dwb.auth.password.dto.ForgotPasswordRequest;
import com.dwb.auth.password.dto.ResetPasswordRequest;
import com.dwb.auth.password.dto.SetPasswordRequest;
import com.dwb.auth.register.entity.EmailOtp;
import com.dwb.auth.register.entity.EmailOtpType;
import com.dwb.auth.register.repository.EmailOtpRepository;
import com.dwb.exception.custom.BadRequestException;
import com.dwb.security.util.SecurityUtils;
import com.dwb.user.entity.User;
import com.dwb.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PasswordServiceImpl implements PasswordService {

    private final UserRepository userRepository;
    private final EmailOtpRepository emailOtpRepository;
    private final PasswordEncoder passwordEncoder;
    private final SecureRandom secureRandom = new SecureRandom();

    @Override
    @Transactional
    public void setPassword(SetPasswordRequest request) {
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new BadRequestException("Passwords do not match");
        }

        String email = SecurityUtils.getCurrentUserEmail();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadRequestException("User not found"));

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setPasswordSet(true);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void forgotPassword(ForgotPasswordRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadRequestException("No account found with this email"));

        String otp = String.format("%06d", secureRandom.nextInt(1_000_000));

        EmailOtp emailOtp = new EmailOtp();
        emailOtp.setUser(user);
        emailOtp.setOtp(otp);
        emailOtp.setExpiresAt(LocalDateTime.now().plusMinutes(10));
        emailOtp.setType(EmailOtpType.PASSWORD_RESET);
        emailOtpRepository.save(emailOtp);

        // Dev mode — replace with Resend.com before production
        System.out.println("=== PASSWORD RESET OTP === Email: " + user.getEmail() + " | OTP: " + otp + " ===");
    }

    @Override
    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new BadRequestException("Passwords do not match");
        }

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadRequestException("No account found with this email"));

        EmailOtp otp = emailOtpRepository
                .findTopByUser_EmailAndVerifiedFalseAndTypeOrderByCreatedAtDesc(
                        request.getEmail(), EmailOtpType.PASSWORD_RESET)
                .orElseThrow(() -> new BadRequestException("No active OTP found. Please request a new one."));

        if (otp.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("OTP has expired. Please request a new one.");
        }

        if (otp.getAttempts() >= 5) {
            throw new BadRequestException("Too many failed attempts. Please request a new OTP.");
        }

        if (!otp.getOtp().equals(request.getOtp())) {
            otp.setAttempts(otp.getAttempts() + 1);
            emailOtpRepository.save(otp);
            throw new BadRequestException("Invalid OTP");
        }

        otp.setVerified(true);
        emailOtpRepository.save(otp);

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setPasswordSet(true);
        user.setEmailVerified(true);
        userRepository.save(user);
    }
}
