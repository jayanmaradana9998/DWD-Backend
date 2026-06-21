package com.dwb.admin.service;

import com.dwb.admin.dto.AdminRetailerResponse;
import com.dwb.admin.dto.AdminUserResponse;
import com.dwb.common.dto.BaseResponse;
import com.dwb.exception.custom.BadRequestException;
import com.dwb.exception.custom.ResourceNotFoundException;
import com.dwb.kyc.repository.KycDocumentRepository;
import com.dwb.retailer.repository.RetailerProfileRepository;
import com.dwb.role.entity.Role;
import com.dwb.user.entity.User;
import com.dwb.user.entity.UserStatus;
import com.dwb.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminUserServiceImpl implements AdminUserService {

    private final UserRepository userRepository;
    private final RetailerProfileRepository retailerProfileRepository;
    private final KycDocumentRepository kycDocumentRepository;

    @Override
    public BaseResponse<List<AdminUserResponse>> getAllUsers() {
        List<AdminUserResponse> users = userRepository.findAll().stream()
                .map(user -> new AdminUserResponse(
                        user.getId(),
                        user.getUniqueId(),
                        user.getFullName(),
                        user.getEmail(),
                        user.getPhoneNumber(),
                        user.getRoles().stream().map(Role::name).collect(Collectors.toSet()),
                        user.getStatus().name(),
                        user.getEmailVerified(),
                        user.getPhoneNumberVerified(),
                        user.getCreatedAt()
                ))
                .collect(Collectors.toList());

        return new BaseResponse<>(true, "Users fetched", users);
    }

    @Override
    @Transactional
    public BaseResponse<Object> blockUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (user.getStatus() == UserStatus.BLOCKED) {
            throw new BadRequestException("User is already blocked");
        }

        user.setStatus(UserStatus.BLOCKED);
        userRepository.save(user);

        return new BaseResponse<>(true, "User blocked successfully", null);
    }

    @Override
    @Transactional
    public BaseResponse<Object> unblockUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (user.getStatus() != UserStatus.BLOCKED) {
            throw new BadRequestException("User is not blocked");
        }

        user.setStatus(UserStatus.ACTIVE);
        userRepository.save(user);

        return new BaseResponse<>(true, "User unblocked successfully", null);
    }

    @Override
    public BaseResponse<List<AdminRetailerResponse>> getAllRetailers() {
        List<AdminRetailerResponse> retailers = retailerProfileRepository.findAll().stream()
                .map(profile -> {
                    User user = profile.getUser();
                    String kycStatus = kycDocumentRepository
                            .findTopByUser_IdOrderByCreatedAtDesc(user.getId())
                            .map(kyc -> kyc.getStatus().name())
                            .orElse("NOT_SUBMITTED");

                    return new AdminRetailerResponse(
                            profile.getId(),
                            profile.getRetailerUniqueId(),
                            profile.getStoreName(),
                            profile.getStoreType(),
                            user.getFullName(),
                            user.getEmail(),
                            user.getPhoneNumber(),
                            profile.getGst(),
                            profile.getPan(),
                            profile.getCity(),
                            profile.getState(),
                            kycStatus,
                            profile.getCreatedAt()
                    );
                })
                .collect(Collectors.toList());

        return new BaseResponse<>(true, "Retailers fetched", retailers);
    }
}
