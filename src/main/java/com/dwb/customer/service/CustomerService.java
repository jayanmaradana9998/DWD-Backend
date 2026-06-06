package com.dwb.customer.service;

import com.dwb.customer.dto.CreateCustomerRequest;
import com.dwb.customer.dto.CustomerResponse;
import com.dwb.customer.dto.VerifyCustomerOtpRequest;

import java.util.List;

public interface CustomerService {

    CustomerResponse createCustomer(CreateCustomerRequest request);

    List<CustomerResponse> searchCustomers(String query);

    void sendOtp(Long customerId);

    void verifyOtp(Long customerId, VerifyCustomerOtpRequest request);
}
