package com.dwb.auth.login.service;

import com.dwb.auth.login.dto.LoginResponse;
import com.dwb.auth.login.dto.SendPhoneLoginOtpRequest;
import com.dwb.auth.login.dto.VerifyPhoneLoginOtpRequest;
import com.dwb.common.dto.BaseResponse;
import com.dwb.exception.custom.BadRequestException;
import com.dwb.otp.entity.PhoneOtp;
import com.dwb.otp.entity.PhoneOtpType;
import com.dwb.otp.repository.PhoneOtpRepository;
import com.dwb.role.entity.Role;
import com.dwb.security.jwt.service.JwtService;
import com.dwb.user.entity.User;
import com.dwb.user.entity.UserStatus;
import com.dwb.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PhoneLoginServiceImpl implements PhoneLoginService {

    private final UserRepository userRepository;
    private final PhoneOtpRepository phoneOtpRepository;
    private final JwtService jwtService;

    private final SecureRandom secureRandom = new SecureRandom();

    @Override
    @Transactional
    public BaseResponse<Object> sendPhoneLoginOtp(SendPhoneLoginOtpRequest request) {

        User user = userRepository.findByPhoneNumber(request.getPhoneNumber())
                .orElseThrow(() -> new BadRequestException("No account found with this phone number"));

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new BadRequestException("Account is not active. Please complete registration first.");
        }

        String otp = generateOtp();

        PhoneOtp phoneOtp = new PhoneOtp();
        phoneOtp.setUser(user);
        phoneOtp.setOtp(otp);
        phoneOtp.setExpiresAt(LocalDateTime.now().plusMinutes(5));
        phoneOtp.setType(PhoneOtpType.LOGIN);

        phoneOtpRepository.save(phoneOtp);

        // Console OTP for development — replace with WhatsApp/SMS in production
        System.out.println("PHONE LOGIN OTP FOR " + user.getPhoneNumber() + " : " + otp);

        return new BaseResponse<>(true, "OTP sent to your phone number", null);
    }

    @Override
    @Transactional
    public BaseResponse<LoginResponse> verifyPhoneLoginOtp(VerifyPhoneLoginOtpRequest request) {

        PhoneOtp phoneOtp = phoneOtpRepository
                .findTopByUser_PhoneNumberAndVerifiedFalseAndTypeOrderByCreatedAtDesc(
                        request.getPhoneNumber(), PhoneOtpType.LOGIN)
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

        String token = jwtService.generateToken(user.getEmail(), user.getRoles());

        Set<String> roleNames = user.getRoles().stream()
                .map(Role::name)
                .collect(Collectors.toSet());

        LoginResponse loginResponse = new LoginResponse(token, user.getUniqueId(), roleNames);

        return new BaseResponse<>(true, "Login successful", loginResponse);
    }

    private String generateOtp() {
        int otp = secureRandom.nextInt(1_000_000);
        return String.format("%06d", otp);
    }
}
