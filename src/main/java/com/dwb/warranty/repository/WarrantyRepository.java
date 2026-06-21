package com.dwb.warranty.repository;

import com.dwb.warranty.entity.Warranty;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WarrantyRepository extends JpaRepository<Warranty, Long> {

    List<Warranty> findByRetailerProfile_IdOrderByCreatedAtDesc(Long retailerProfileId);

    Optional<Warranty> findByIdAndRetailerProfile_Id(Long id, Long retailerProfileId);

    boolean existsByInvoice_Id(Long invoiceId);

    long countByRetailerProfile_Id(Long retailerProfileId);
}
