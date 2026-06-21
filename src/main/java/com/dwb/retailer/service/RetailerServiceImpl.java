package com.dwb.retailer.service;

import com.dwb.common.dto.BaseResponse;
import com.dwb.exception.custom.BadRequestException;
import com.dwb.retailer.dto.RetailerRegisterRequest;
import com.dwb.retailer.dto.RetailerRegisterResponse;
import com.dwb.retailer.entity.RetailerProfile;
import com.dwb.retailer.repository.RetailerProfileRepository;
import com.dwb.role.entity.Role;
import com.dwb.security.util.SecurityUtils;
import com.dwb.user.entity.User;
import com.dwb.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RetailerServiceImpl implements RetailerService {

    private final UserRepository userRepository;
    private final RetailerProfileRepository retailerProfileRepository;

    @Override
    @Transactional
    public BaseResponse<RetailerRegisterResponse> register(RetailerRegisterRequest request) {

        String email = SecurityUtils.getCurrentUserEmail();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadRequestException("User not found"));

        // Prevent duplicate retailer profiles
        if (retailerProfileRepository.existsByUser_Id(user.getId())) {
            throw new BadRequestException("Retailer profile already exists for this account");
        }

        // GST and PAN must be unique across the platform
        if (retailerProfileRepository.existsByGst(request.getGst())) {
            throw new BadRequestException("A retailer with this GST number already exists");
        }

        if (retailerProfileRepository.existsByPan(request.getPan())) {
            throw new BadRequestException("A retailer with this PAN number already exists");
        }

        RetailerProfile profile = new RetailerProfile();
        profile.setUser(user);
        profile.setStoreName(request.getStoreName());
        profile.setStoreType(request.getStoreType());
        profile.setAddress(request.getAddress());
        profile.setCity(request.getCity());
        profile.setState(request.getState());
        profile.setPincode(request.getPincode());
        profile.setGst(request.getGst());
        profile.setPan(request.getPan());
        profile.setOperatingHours(request.getOperatingHours());

        // First save to get the auto-generated database ID
        RetailerProfile saved = retailerProfileRepository.save(profile);

        // Now generate the human-readable unique ID using the DB id
        saved.setRetailerUniqueId("RET" + String.format("%06d", saved.getId()));
        retailerProfileRepository.save(saved);

        // Add RETAILER role to the user's roles
        user.getRoles().add(Role.RETAILER);
        userRepository.save(user);

        return new BaseResponse<>(
                true,
                "Retailer profile created successfully",
                buildResponse(saved, user)
        );
    }

    @Override
    public BaseResponse<RetailerRegisterResponse> getProfile() {

        String email = SecurityUtils.getCurrentUserEmail();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadRequestException("User not found"));

        RetailerProfile profile = retailerProfileRepository.findByUser_Id(user.getId())
                .orElseThrow(() -> new BadRequestException("Retailer profile not found"));

        return new BaseResponse<>(true, "Profile fetched successfully", buildResponse(profile, user));
    }

    private RetailerRegisterResponse buildResponse(RetailerProfile profile, User user) {
        return new RetailerRegisterResponse(
                profile.getId(),
                profile.getRetailerUniqueId(),
                profile.getStoreName(),
                user.getFullName(),
                user.getEmail(),
                user.getPhoneNumber()
        );
    }
}
