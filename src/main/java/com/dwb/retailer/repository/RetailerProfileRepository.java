package com.dwb.retailer.repository;

import com.dwb.retailer.entity.RetailerProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RetailerProfileRepository extends JpaRepository<RetailerProfile, Long> {

    Optional<RetailerProfile> findByUser_Id(Long userId);

    boolean existsByUser_Id(Long userId);

    boolean existsByGst(String gst);

    boolean existsByPan(String pan);
}
