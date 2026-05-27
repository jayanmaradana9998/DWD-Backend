package com.dwb.auth.service;

import com.dwb.auth.dto.RegisterRequest;
import com.dwb.auth.dto.VerifyEmailOtpRequest;
import com.dwb.auth.entity.EmailOtp;
import com.dwb.auth.repository.EmailOtpRepository;
import com.dwb.common.dto.BaseResponse;
import com.dwb.exception.custom.BadRequestException;
import com.dwb.role.entity.Role;
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
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final EmailOtpRepository emailOtpRepository;
    private final PasswordEncoder passwordEncoder;

    private final SecureRandom secureRandom = new SecureRandom();

    @Override
    @Transactional
    public BaseResponse<Object> register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already exists");
        }

        if (userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new BadRequestException("Phone number already exists");
        }

        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new BadRequestException("Password and confirm password do not match");
        }

        User user = new User();
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.RETAILER); // keep retailer for now
        user.setStatus(UserStatus.PENDING);
        user.setEmailVerified(false);
        user.setPhoneNumberVerified(false);
        user.setUniqueId(null);

        User savedUser = userRepository.save(user);

        String otp = generateOtp();

        EmailOtp emailOtp = new EmailOtp();
        emailOtp.setUser(savedUser);
        emailOtp.setOtp(otp);
        emailOtp.setExpiresAt(LocalDateTime.now().plusMinutes(5));
        emailOtp.setVerified(false);
        emailOtp.setAttempts(0);

        emailOtpRepository.save(emailOtp);

        System.out.println("EMAIL OTP FOR " + savedUser.getEmail() + " : " + otp);

        return new BaseResponse<>(
                true,
                "Registration successful. Email OTP sent.",
                null
        );
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

        User user = emailOtp.getUser();
        user.setEmailVerified(true);
        user.setStatus(UserStatus.ACTIVE);
        user.setUniqueId(generateUniqueId(user));

        userRepository.save(user);

        return new BaseResponse<>(
                true,
                "Email verified successfully. Account activated.",
                null
        );
    }

    private String generateOtp() {
        int otp = secureRandom.nextInt(1_000_000);
        return String.format("%06d", otp);
    }

    private String generateUniqueId(User user) {
        String prefix = switch (user.getRole()) {
            case RETAILER -> "RET";
            case CUSTOMER -> "CUS";
            case TECHNICIAN -> "TEC";
            case ADMIN -> "ADM";
        };

        return prefix + String.format("%06d", user.getId());
    }
}