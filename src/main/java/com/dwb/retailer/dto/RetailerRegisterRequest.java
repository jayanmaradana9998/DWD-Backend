package com.dwb.retailer.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RetailerRegisterRequest {

    @NotBlank(message = "Store name is required")
    private String storeName;

    @NotBlank(message = "Store type is required")
    private String storeType;

    @NotBlank(message = "Address is required")
    private String address;

    @NotBlank(message = "City is required")
    private String city;

    @NotBlank(message = "State is required")
    private String state;

    @NotBlank(message = "Pincode is required")
    @Pattern(regexp = "^\\d{6}$", message = "Pincode must be exactly 6 digits")
    private String pincode;

    @NotBlank(message = "GST number is required")
    @Pattern(
            regexp = "^[0-9]{2}[A-Z]{5}[0-9]{4}[A-Z]{1}[1-9A-Z]{1}Z[0-9A-Z]{1}$",
            message = "Enter a valid GST number (e.g. 22AAAAA0000A1Z5)"
    )
    private String gst;

    @NotBlank(message = "PAN number is required")
    @Pattern(
            regexp = "^[A-Z]{5}[0-9]{4}[A-Z]{1}$",
            message = "Enter a valid PAN number (e.g. ABCDE1234F)"
    )
    private String pan;

    // Optional — e.g. Mon-Fri 9AM-6PM
    private String operatingHours;
}
