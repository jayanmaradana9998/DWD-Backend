package com.dwb.agent.repository;

import com.dwb.agent.entity.Agent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AgentRepository extends JpaRepository<Agent, Long> {

    List<Agent> findByRetailerProfile_Id(Long retailerProfileId);

    boolean existsByRetailerProfile_IdAndEmail(Long retailerProfileId, String email);

    boolean existsByRetailerProfile_IdAndPhoneNumber(Long retailerProfileId, String phoneNumber);

    Optional<Agent> findByIdAndRetailerProfile_Id(Long id, Long retailerProfileId);
}
