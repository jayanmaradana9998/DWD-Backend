package com.dwb.kyc.service;

import com.dwb.common.dto.BaseResponse;
import com.dwb.exception.custom.BadRequestException;
import com.dwb.kyc.dto.KycStatusResponse;
import com.dwb.kyc.entity.KycDocument;
import com.dwb.kyc.entity.KycStatus;
import com.dwb.kyc.repository.KycDocumentRepository;
import com.dwb.security.util.SecurityUtils;
import com.dwb.storage.service.StorageService;
import com.dwb.user.entity.User;
import com.dwb.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class KycServiceImpl implements KycService {

    private final UserRepository userRepository;
    private final KycDocumentRepository kycDocumentRepository;
    private final StorageService storageService;

    @Override
    @Transactional
    public BaseResponse<Object> submitKyc(
            String fullName,
            String idType,
            String idNumber,
            String gstNumber,
            List<MultipartFile> documents
    ) {
        String email = SecurityUtils.getCurrentUserEmail();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadRequestException("User not found"));

        // Allow resubmission only if previous KYC was REJECTED
        kycDocumentRepository.findTopByUser_IdOrderByCreatedAtDesc(user.getId())
                .ifPresent(existing -> {
                    if (existing.getStatus() == KycStatus.PENDING) {
                        throw new BadRequestException("KYC already submitted and is under review");
                    }
                    if (existing.getStatus() == KycStatus.APPROVED) {
                        throw new BadRequestException("KYC already approved");
                    }
                });

        if (documents == null || documents.isEmpty()) {
            throw new BadRequestException("At least one document is required");
        }

        // Save each uploaded file and collect their storage paths
        List<String> savedPaths = new ArrayList<>();
        for (MultipartFile doc : documents) {
            String subFolder = "kyc/" + user.getId();
            String path = storageService.store(doc, subFolder);
            savedPaths.add(path);
        }

        KycDocument kyc = new KycDocument();
        kyc.setUser(user);
        kyc.setFullName(fullName);
        kyc.setIdType(idType);
        kyc.setIdNumber(idNumber);
        kyc.setGstNumber(gstNumber);
        kyc.setDocumentPaths(savedPaths);
        kyc.setStatus(KycStatus.PENDING);

        kycDocumentRepository.save(kyc);

        return new BaseResponse<>(true, "KYC submitted successfully. Under review.", null);
    }

    @Override
    public BaseResponse<KycStatusResponse> getStatus() {

        String email = SecurityUtils.getCurrentUserEmail();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadRequestException("User not found"));

        return kycDocumentRepository
                .findTopByUser_IdOrderByCreatedAtDesc(user.getId())
                .map(kyc -> new BaseResponse<>(
                        true,
                        "KYC status fetched",
                        new KycStatusResponse(
                                kyc.getStatus().name(),
                                kyc.getIdType(),
                                kyc.getFullName(),
                                kyc.getCreatedAt()
                        )
                ))
                .orElse(new BaseResponse<>(
                        true,
                        "KYC not submitted yet",
                        new KycStatusResponse("NOT_SUBMITTED", null, null, null)
                ));
    }
}
