package com.dwb.template.repository;

import com.dwb.template.entity.RetailerTemplateSettings;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RetailerTemplateSettingsRepository extends JpaRepository<RetailerTemplateSettings, Long> {

    Optional<RetailerTemplateSettings> findByRetailerProfile_Id(Long retailerProfileId);
}
