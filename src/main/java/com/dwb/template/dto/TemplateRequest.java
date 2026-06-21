package com.dwb.template.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TemplateRequest {

    @NotBlank(message = "Invoice template type is required")
    private String invoiceTemplateType;

    @NotBlank(message = "Warranty template type is required")
    private String warrantyTemplateType;

    private String primaryColor;

    private String footerText;
}
