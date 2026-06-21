package com.dwb.agent.service;

import com.dwb.agent.dto.AgentRequest;
import com.dwb.agent.dto.AgentResponse;
import com.dwb.agent.dto.BulkAgentRequest;
import com.dwb.agent.entity.Agent;
import com.dwb.agent.repository.AgentRepository;
import com.dwb.common.dto.BaseResponse;
import com.dwb.exception.custom.BadRequestException;
import com.dwb.retailer.entity.RetailerProfile;
import com.dwb.retailer.repository.RetailerProfileRepository;
import com.dwb.security.util.SecurityUtils;
import com.dwb.user.entity.User;
import com.dwb.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AgentServiceImpl implements AgentService {

    private final UserRepository userRepository;
    private final RetailerProfileRepository retailerProfileRepository;
    private final AgentRepository agentRepository;

    @Override
    public BaseResponse<List<AgentResponse>> getAgents() {
        RetailerProfile profile = getRetailerProfileForCurrentUser();

        List<AgentResponse> agents = agentRepository.findByRetailerProfile_Id(profile.getId())
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        return new BaseResponse<>(true, "Agents retrieved successfully", agents);
    }

    @Override
    @Transactional
    public BaseResponse<List<AgentResponse>> createAgents(BulkAgentRequest request) {
        RetailerProfile profile = getRetailerProfileForCurrentUser();

        if (request.getAgents() == null || request.getAgents().isEmpty()) {
            throw new BadRequestException("At least one agent is required");
        }

        request.getAgents().forEach(agentRequest -> validateDuplicateAgent(agentRequest, profile));

        List<Agent> saved = request.getAgents().stream()
                .map(item -> {
                    Agent agent = new Agent();
                    agent.setRetailerProfile(profile);
                    agent.setName(item.getName());
                    agent.setEmail(item.getEmail());
                    agent.setPhoneNumber(item.getPhoneNumber());
                    agent.setAccessLevel(item.getAccessLevel());
                    agent.setActive(item.getActive());
                    return agentRepository.save(agent);
                })
                .collect(Collectors.toList());

        return new BaseResponse<>(
                true,
                "Agents created successfully",
                saved.stream().map(this::toResponse).collect(Collectors.toList())
        );
    }

    @Override
    @Transactional
    public BaseResponse<AgentResponse> updateAgent(Long agentId, AgentRequest request) {
        RetailerProfile profile = getRetailerProfileForCurrentUser();
        Agent agent = agentRepository.findByIdAndRetailerProfile_Id(agentId, profile.getId())
                .orElseThrow(() -> new BadRequestException("Agent not found"));

        if (!agent.getEmail().equals(request.getEmail())
                && agentRepository.existsByRetailerProfile_IdAndEmail(profile.getId(), request.getEmail())) {
            throw new BadRequestException("Email already used by another agent");
        }

        if (!agent.getPhoneNumber().equals(request.getPhoneNumber())
                && agentRepository.existsByRetailerProfile_IdAndPhoneNumber(profile.getId(), request.getPhoneNumber())) {
            throw new BadRequestException("Phone number already used by another agent");
        }

        agent.setName(request.getName());
        agent.setEmail(request.getEmail());
        agent.setPhoneNumber(request.getPhoneNumber());
        agent.setAccessLevel(request.getAccessLevel());
        agent.setActive(request.getActive());

        return new BaseResponse<>(true, "Agent updated successfully", toResponse(agentRepository.save(agent)));
    }

    @Override
    @Transactional
    public BaseResponse<Object> deleteAgent(Long agentId) {
        RetailerProfile profile = getRetailerProfileForCurrentUser();
        Agent agent = agentRepository.findByIdAndRetailerProfile_Id(agentId, profile.getId())
                .orElseThrow(() -> new BadRequestException("Agent not found"));
        agentRepository.delete(agent);
        return new BaseResponse<>(true, "Agent deleted successfully", null);
    }

    private RetailerProfile getRetailerProfileForCurrentUser() {
        String email = SecurityUtils.getCurrentUserEmail();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadRequestException("User not found"));

        return retailerProfileRepository.findByUser_Id(user.getId())
                .orElseThrow(() -> new BadRequestException("Retailer profile not found"));
    }

    private void validateDuplicateAgent(AgentRequest request, RetailerProfile profile) {
        if (agentRepository.existsByRetailerProfile_IdAndEmail(profile.getId(), request.getEmail())) {
            throw new BadRequestException("An agent with this email already exists");
        }

        if (agentRepository.existsByRetailerProfile_IdAndPhoneNumber(profile.getId(), request.getPhoneNumber())) {
            throw new BadRequestException("An agent with this phone number already exists");
        }
    }

    private AgentResponse toResponse(Agent agent) {
        return new AgentResponse(
                agent.getId(),
                agent.getName(),
                agent.getEmail(),
                agent.getPhoneNumber(),
                agent.getAccessLevel(),
                agent.getActive(),
                agent.getCreatedAt(),
                agent.getUpdatedAt()
        );
    }
}
