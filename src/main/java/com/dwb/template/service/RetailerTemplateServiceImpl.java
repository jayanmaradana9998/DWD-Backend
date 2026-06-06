package com.dwb.template.service;

import com.dwb.common.dto.BaseResponse;
import com.dwb.exception.custom.BadRequestException;
import com.dwb.retailer.entity.RetailerProfile;
import com.dwb.retailer.repository.RetailerProfileRepository;
import com.dwb.security.util.SecurityUtils;
import com.dwb.storage.service.StorageService;
import com.dwb.template.dto.TemplateRequest;
import com.dwb.template.dto.TemplateResponse;
import com.dwb.template.entity.RetailerTemplateSettings;
import com.dwb.template.repository.RetailerTemplateSettingsRepository;
import com.dwb.user.entity.User;
import com.dwb.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class RetailerTemplateServiceImpl implements RetailerTemplateService {

    private final UserRepository userRepository;
    private final RetailerProfileRepository retailerProfileRepository;
    private final RetailerTemplateSettingsRepository templateRepository;
    private final StorageService storageService;

    @Override
    public BaseResponse<TemplateResponse> getTemplate() {
        RetailerProfile profile = getRetailerProfileForCurrentUser();

        return templateRepository.findByRetailerProfile_Id(profile.getId())
                .map(template -> new BaseResponse<>(true, "Template settings fetched successfully", toResponse(template)))
                .orElse(new BaseResponse<>(true, "Template settings not configured", null));
    }

    @Override
    @Transactional
    public BaseResponse<TemplateResponse> saveTemplate(TemplateRequest request, MultipartFile storeLogo) {
        RetailerProfile profile = getRetailerProfileForCurrentUser();

        RetailerTemplateSettings settings = templateRepository.findByRetailerProfile_Id(profile.getId())
                .orElse(new RetailerTemplateSettings());

        settings.setRetailerProfile(profile);
        settings.setInvoiceTemplateType(request.getInvoiceTemplateType());
        settings.setWarrantyTemplateType(request.getWarrantyTemplateType());
        settings.setPrimaryColor(request.getPrimaryColor());
        settings.setFooterText(request.getFooterText());

        if (storeLogo != null && !storeLogo.isEmpty()) {
            String storedPath = storageService.store(storeLogo, "templates/" + profile.getId());
            settings.setStoreLogoPath(storedPath);
        }

        RetailerTemplateSettings saved = templateRepository.save(settings);
        return new BaseResponse<>(true, "Template settings saved successfully", toResponse(saved));
    }

    private RetailerProfile getRetailerProfileForCurrentUser() {
        String email = SecurityUtils.getCurrentUserEmail();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadRequestException("User not found"));

        return retailerProfileRepository.findByUser_Id(user.getId())
                .orElseThrow(() -> new BadRequestException("Retailer profile not found"));
    }

    private TemplateResponse toResponse(RetailerTemplateSettings settings) {
        return new TemplateResponse(
                settings.getId(),
                settings.getInvoiceTemplateType(),
                settings.getWarrantyTemplateType(),
                settings.getStoreLogoPath(),
                settings.getPrimaryColor(),
                settings.getFooterText()
        );
    }
}
