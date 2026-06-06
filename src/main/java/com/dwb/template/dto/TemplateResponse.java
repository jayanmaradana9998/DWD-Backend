package com.dwb.template.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TemplateResponse {

    private Long id;
    private String invoiceTemplateType;
    private String warrantyTemplateType;
    private String storeLogoPath;
    private String primaryColor;
    private String footerText;
}
