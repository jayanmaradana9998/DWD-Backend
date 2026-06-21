package com.dwb.agent.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class BulkAgentRequest {

    @NotEmpty(message = "At least one agent is required")
    @Valid
    private List<AgentRequest> agents;
}
