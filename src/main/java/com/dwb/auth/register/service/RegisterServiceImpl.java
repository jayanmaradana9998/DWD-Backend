package com.dwb.auth.register.service;

import com.dwb.auth.register.dto.RegisterRequest;
import com.dwb.auth.register.dto.VerifyEmailOtpRequest;
import com.dwb.auth.register.entity.EmailOtp;
import com.dwb.auth.register.repository.EmailOtpRepository;
import com.dwb.common.dto.BaseResponse;
import com.dwb.exception.custom.BadRequestException;
import com.dwb.user.entity.User;
import com.dwb.user.entity.UserStatus;
import com.dwb.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class RegisterServiceImpl implements RegisterService {

    private final UserRepository userRepository;
    private final EmailOtpRepository emailOtpRepository;
    private final PasswordEncoder passwordEncoder;

    private final SecureRandom secureRandom = new SecureRandom();

    @Override
    @Transactional
    public BaseResponse<Object> register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already registered");
        }

        if (userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new BadRequestException("Phone number already registered");
        }

        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new BadRequestException("Passwords do not match");
        }

        User user = new User();
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setStatus(UserStatus.PENDING);
        // roles stays empty — user picks role after full verification

        User savedUser = userRepository.save(user);

        String otp = generateOtp();

        EmailOtp emailOtp = new EmailOtp();
        emailOtp.setUser(savedUser);
        emailOtp.setOtp(otp);
        emailOtp.setExpiresAt(LocalDateTime.now().plusMinutes(5));

        emailOtpRepository.save(emailOtp);

        System.out.println("EMAIL OTP FOR " + savedUser.getEmail() + " : " + otp);

        return new BaseResponse<>(true, "Registration successful. Please verify your email.", null);
    }

    @Override
    @Transactional
    public BaseResponse<Object> verifyEmailOtp(VerifyEmailOtpRequest request) {

        EmailOtp emailOtp = emailOtpRepository
                .findTopByUser_EmailAndVerifiedFalseOrderByCreatedAtDesc(request.getEmail())
                .orElseThrow(() -> new BadRequestException("No active OTP found for this email"));

        if (emailOtp.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("OTP has expired");
        }

        if (!emailOtp.getOtp().equals(request.getOtp())) {
            emailOtp.setAttempts(emailOtp.getAttempts() + 1);
            emailOtpRepository.save(emailOtp);
            throw new BadRequestException("Invalid OTP");
        }

        emailOtp.setVerified(true);
        emailOtpRepository.save(emailOtp);

        // Only mark email as verified — account stays PENDING until phone is also verified
        User user = emailOtp.getUser();
        user.setEmailVerified(true);
        userRepository.save(user);

        return new BaseResponse<>(true, "Email verified successfully. Please verify your phone number.", null);
    }

    private String generateOtp() {
        int otp = secureRandom.nextInt(1_000_000);
        return String.format("%06d", otp);
    }
}
