package com.dwb.agent.service;

import com.dwb.agent.dto.AgentRequest;
import com.dwb.agent.dto.AgentResponse;
import com.dwb.agent.dto.BulkAgentRequest;
import com.dwb.common.dto.BaseResponse;

import java.util.List;

public interface AgentService {

    BaseResponse<List<AgentResponse>> getAgents();

    BaseResponse<List<AgentResponse>> createAgents(BulkAgentRequest request);

    BaseResponse<AgentResponse> updateAgent(Long agentId, AgentRequest request);

    BaseResponse<Object> deleteAgent(Long agentId);
}
