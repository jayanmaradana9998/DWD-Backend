package com.dwb.admin.service;

import com.dwb.admin.dto.KycDetailResponse;
import com.dwb.admin.dto.KycListItemResponse;
import com.dwb.common.dto.BaseResponse;
import com.dwb.exception.custom.BadRequestException;
import com.dwb.exception.custom.ResourceNotFoundException;
import com.dwb.kyc.entity.KycDocument;
import com.dwb.kyc.entity.KycStatus;
import com.dwb.kyc.repository.KycDocumentRepository;
import com.dwb.role.entity.Role;
import com.dwb.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminKycServiceImpl implements AdminKycService {

    private final KycDocumentRepository kycDocumentRepository;

    @Override
    public BaseResponse<List<KycListItemResponse>> getAllKyc(String status) {
        List<KycDocument> docs;

        if (status != null && !status.isBlank()) {
            KycStatus kycStatus;
            try {
                kycStatus = KycStatus.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new BadRequestException("Invalid status filter. Use: PENDING, APPROVED, REJECTED");
            }
            docs = kycDocumentRepository.findAllByStatus(kycStatus);
        } else {
            docs = kycDocumentRepository.findAll();
        }

        List<KycListItemResponse> result = docs.stream()
                .map(doc -> new KycListItemResponse(
                        doc.getId(),
                        doc.getUser().getId(),
                        doc.getUser().getUniqueId(),
                        doc.getFullName(),
                        doc.getIdType(),
                        doc.getStatus().name(),
                        doc.getCreatedAt()
                ))
                .collect(Collectors.toList());

        return new BaseResponse<>(true, "KYC list fetched", result);
    }

    @Override
    public BaseResponse<KycDetailResponse> getKycDetail(Long kycId) {
        KycDocument doc = kycDocumentRepository.findById(kycId)
                .orElseThrow(() -> new ResourceNotFoundException("KYC submission not found"));

        User user = doc.getUser();

        return new BaseResponse<>(true, "KYC detail fetched", new KycDetailResponse(
                doc.getId(),
                user.getId(),
                user.getUniqueId(),
                user.getEmail(),
                user.getPhoneNumber(),
                doc.getFullName(),
                doc.getIdType(),
                doc.getIdNumber(),
                doc.getGstNumber(),
                doc.getDocumentPaths(),
                doc.getStatus().name(),
                doc.getRejectionReason(),
                doc.getCreatedAt()
        ));
    }

    @Override
    @Transactional
    public BaseResponse<Object> approveKyc(Long kycId) {
        KycDocument doc = kycDocumentRepository.findById(kycId)
                .orElseThrow(() -> new ResourceNotFoundException("KYC submission not found"));

        if (doc.getStatus() == KycStatus.APPROVED) {
            throw new BadRequestException("KYC is already approved");
        }

        doc.setStatus(KycStatus.APPROVED);
        doc.setRejectionReason(null);
        kycDocumentRepository.save(doc);

        return new BaseResponse<>(true, "KYC approved successfully", null);
    }

    @Override
    @Transactional
    public BaseResponse<Object> rejectKyc(Long kycId, String reason) {
        KycDocument doc = kycDocumentRepository.findById(kycId)
                .orElseThrow(() -> new ResourceNotFoundException("KYC submission not found"));

        if (doc.getStatus() == KycStatus.REJECTED) {
            throw new BadRequestException("KYC is already rejected");
        }

        doc.setStatus(KycStatus.REJECTED);
        doc.setRejectionReason(reason);
        kycDocumentRepository.save(doc);

        return new BaseResponse<>(true, "KYC rejected", null);
    }
}
