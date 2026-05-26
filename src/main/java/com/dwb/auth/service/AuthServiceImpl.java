package com.dwb.auth.service;

import com.dwb.auth.dto.RegisterRequest;
import com.dwb.common.dto.BaseResponse;
import com.dwb.exception.custom.BadRequestException;
import com.dwb.role.entity.Role;
import com.dwb.user.entity.User;
import com.dwb.user.entity.UserStatus;
import com.dwb.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
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
        user.setRole(Role.RETAILER);   // for now, starting from retailer
        user.setStatus(UserStatus.PENDING);
        user.setEmailVerified(false);
        user.setPhoneNumberVerified(false);
        user.setUniqueId(null);

        userRepository.save(user);

        return new BaseResponse<>(
                true,
                "User registered successfully. Please verify your email.",
                null
        );
    }
}