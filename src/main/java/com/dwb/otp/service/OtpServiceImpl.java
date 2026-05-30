package com.dwb.otp.service;

import com.dwb.common.dto.BaseResponse;
import com.dwb.exception.custom.BadRequestException;
import com.dwb.otp.dto.SendPhoneOtpRequest;
import com.dwb.otp.dto.VerifyPhoneOtpRequest;
import com.dwb.otp.entity.PhoneOtp;
import com.dwb.otp.entity.PhoneOtpType;
import com.dwb.otp.repository.PhoneOtpRepository;
import com.dwb.user.entity.User;
import com.dwb.user.entity.UserStatus;
import com.dwb.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OtpServiceImpl implements OtpService {

    private final UserRepository userRepository;
    private final PhoneOtpRepository phoneOtpRepository;

    private final SecureRandom secureRandom = new SecureRandom();

    @Override
    @Transactional
    public BaseResponse<Object> sendPhoneOtp(SendPhoneOtpRequest request) {

        User user = userRepository.findByPhoneNumber(request.getPhoneNumber())
                .orElseThrow(() -> new BadRequestException("No account found with this phone number"));

        if (user.getPhoneNumberVerified()) {
            throw new BadRequestException("Phone number is already verified");
        }

        String otp = generateOtp();

        PhoneOtp phoneOtp = new PhoneOtp();
        phoneOtp.setUser(user);
        phoneOtp.setOtp(otp);
        phoneOtp.setExpiresAt(LocalDateTime.now().plusMinutes(5));
        phoneOtp.setType(PhoneOtpType.REGISTRATION);

        phoneOtpRepository.save(phoneOtp);

        // Console OTP for development — replace with WhatsApp/SMS in production
        System.out.println("PHONE OTP FOR " + user.getPhoneNumber() + " : " + otp);

        return new BaseResponse<>(true, "OTP sent to your phone number", null);
    }

    @Override
    @Transactional
    public BaseResponse<Object> verifyPhoneOtp(VerifyPhoneOtpRequest request) {

        PhoneOtp phoneOtp = phoneOtpRepository
                .findTopByUser_PhoneNumberAndVerifiedFalseAndTypeOrderByCreatedAtDesc(
                        request.getPhoneNumber(), PhoneOtpType.REGISTRATION)
                .orElseThrow(() -> new BadRequestException("No active OTP found for this phone number"));

        if (phoneOtp.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("OTP has expired");
        }

        if (!phoneOtp.getOtp().equals(request.getOtp())) {
            phoneOtp.setAttempts(phoneOtp.getAttempts() + 1);
            phoneOtpRepository.save(phoneOtp);
            throw new BadRequestException("Invalid OTP");
        }

        phoneOtp.setVerified(true);
        phoneOtpRepository.save(phoneOtp);

        User user = phoneOtp.getUser();
        user.setPhoneNumberVerified(true);

        // Both email and phone are now verified — activate account and generate uniqueId
        if (Boolean.TRUE.equals(user.getEmailVerified())) {
            user.setStatus(UserStatus.ACTIVE);
            user.setUniqueId(generateUniqueId(user));
        }

        userRepository.save(user);

        return new BaseResponse<>(true, "Phone verified successfully. Your account is now active.", null);
    }

    private String generateOtp() {
        int otp = secureRandom.nextInt(1_000_000);
        return String.format("%06d", otp);
    }

    // USR000001, USR000002, etc.
    private String generateUniqueId(User user) {
        return "USR" + String.format("%06d", user.getId());
    }
}
