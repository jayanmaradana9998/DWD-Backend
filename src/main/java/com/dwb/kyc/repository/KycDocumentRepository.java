package com.dwb.kyc.repository;

import com.dwb.kyc.entity.KycDocument;
import com.dwb.kyc.entity.KycStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface KycDocumentRepository extends JpaRepository<KycDocument, Long> {

    Optional<KycDocument> findTopByUser_IdOrderByCreatedAtDesc(Long userId);

    boolean existsByUser_Id(Long userId);

    List<KycDocument> findAllByStatus(KycStatus status);

    long countByStatus(KycStatus status);
}
