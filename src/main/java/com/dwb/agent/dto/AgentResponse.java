package com.dwb.agent.dto;

import com.dwb.agent.entity.AgentAccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class AgentResponse {

    private Long id;
    private String name;
    private String email;
    private String phoneNumber;
    private AgentAccessLevel accessLevel;
    private Boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
