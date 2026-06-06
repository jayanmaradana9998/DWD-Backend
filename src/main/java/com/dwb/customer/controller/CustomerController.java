package com.dwb.customer.controller;

import com.dwb.common.dto.BaseResponse;
import com.dwb.customer.dto.CreateCustomerRequest;
import com.dwb.customer.dto.CustomerResponse;
import com.dwb.customer.dto.VerifyCustomerOtpRequest;
import com.dwb.customer.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
@Tag(name = "Customers", description = "Customer management for retailers")
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping
    @Operation(summary = "Create a new customer under the logged-in retailer")
    public ResponseEntity<BaseResponse<CustomerResponse>> createCustomer(
            @Valid @RequestBody CreateCustomerRequest request) {

        CustomerResponse customer = customerService.createCustomer(request);
        return ResponseEntity.ok(new BaseResponse<>(true, "Customer created successfully", customer));
    }

    @GetMapping("/search")
    @Operation(summary = "Search customers by phone (partial) or email — used for type-ahead in New Bill flow")
    public ResponseEntity<BaseResponse<List<CustomerResponse>>> searchCustomers(
            @RequestParam String q) {

        List<CustomerResponse> results = customerService.searchCustomers(q);
        return ResponseEntity.ok(new BaseResponse<>(true, "Search results", results));
    }

    @PostMapping("/{id}/send-otp")
    @Operation(summary = "Send OTP to customer phone for verification")
    public ResponseEntity<BaseResponse<Void>> sendOtp(@PathVariable Long id) {
        customerService.sendOtp(id);
        return ResponseEntity.ok(new BaseResponse<>(true, "OTP sent successfully", null));
    }

    @PostMapping("/{id}/verify-otp")
    @Operation(summary = "Verify customer phone OTP")
    public ResponseEntity<BaseResponse<Void>> verifyOtp(
            @PathVariable Long id,
            @Valid @RequestBody VerifyCustomerOtpRequest request) {

        customerService.verifyOtp(id, request);
        return ResponseEntity.ok(new BaseResponse<>(true, "Phone verified successfully", null));
    }
}
