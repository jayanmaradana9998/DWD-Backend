package com.dwb.invoice.repository;

import com.dwb.invoice.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    List<Invoice> findByRetailerProfile_IdOrderByCreatedAtDesc(Long retailerProfileId);

    Optional<Invoice> findByIdAndRetailerProfile_Id(Long id, Long retailerProfileId);

    long countByRetailerProfile_Id(Long retailerProfileId);
}
