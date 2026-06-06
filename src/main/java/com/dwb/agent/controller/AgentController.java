package com.dwb.agent.controller;

import com.dwb.agent.dto.AgentRequest;
import com.dwb.agent.dto.AgentResponse;
import com.dwb.agent.dto.BulkAgentRequest;
import com.dwb.agent.service.AgentService;
import com.dwb.common.dto.BaseResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/retailer/agents")
@RequiredArgsConstructor
public class AgentController {

    private final AgentService agentService;

    @GetMapping
    public BaseResponse<List<AgentResponse>> getAgents() {
        return agentService.getAgents();
    }

    @PostMapping
    public BaseResponse<List<AgentResponse>> createAgents(
            @Valid @RequestBody BulkAgentRequest request) {
        return agentService.createAgents(request);
    }

    @PutMapping("/{agentId}")
    public BaseResponse<AgentResponse> updateAgent(
            @PathVariable Long agentId,
            @Valid @RequestBody AgentRequest request) {
        return agentService.updateAgent(agentId, request);
    }

    @DeleteMapping("/{agentId}")
    public BaseResponse<Object> deleteAgent(@PathVariable Long agentId) {
        return agentService.deleteAgent(agentId);
    }
}
