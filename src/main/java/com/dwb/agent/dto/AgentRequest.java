package com.dwb.agent.dto;

import com.dwb.agent.entity.AgentAccessLevel;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AgentRequest {

    @NotBlank(message = "Agent name is required")
    private String name;

    @NotBlank(message = "Agent email is required")
    @Email(message = "Enter a valid email")
    private String email;

    @NotBlank(message = "Agent phone number is required")
    private String phoneNumber;

    @NotNull(message = "Access level is required")
    private AgentAccessLevel accessLevel;

    @NotNull(message = "Account status is required")
    private Boolean active = true;
}
