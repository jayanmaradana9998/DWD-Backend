package com.dwb.customer.service;

import com.dwb.customer.dto.CreateCustomerRequest;
import com.dwb.customer.dto.CustomerResponse;
import com.dwb.customer.dto.VerifyCustomerOtpRequest;
import com.dwb.customer.entity.Customer;
import com.dwb.customer.entity.CustomerOtp;
import com.dwb.customer.repository.CustomerOtpRepository;
import com.dwb.customer.repository.CustomerRepository;
import com.dwb.exception.custom.BadRequestException;
import com.dwb.exception.custom.ResourceNotFoundException;
import com.dwb.retailer.entity.RetailerProfile;
import com.dwb.retailer.repository.RetailerProfileRepository;
import com.dwb.security.util.SecurityUtils;
import com.dwb.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerOtpRepository customerOtpRepository;
    private final RetailerProfileRepository retailerProfileRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public CustomerResponse createCustomer(CreateCustomerRequest request) {
        RetailerProfile retailer = getRetailerProfile();

        if (customerRepository.existsByPhoneAndRetailerProfile_Id(request.getPhone(), retailer.getId())) {
            throw new BadRequestException("A customer with this phone number already exists under your account");
        }

        Customer customer = new Customer();
        customer.setRetailerProfile(retailer);
        customer.setName(request.getName());
        customer.setPhone(request.getPhone());
        customer.setEmail(request.getEmail());

        // First save to get the DB-generated id
        customer = customerRepository.save(customer);

        // Generate uniqueId using the id
        customer.setUniqueId("RCUS" + String.format("%06d", customer.getId()));
        customer = customerRepository.save(customer);

        return toResponse(customer);
    }

    @Override
    public List<CustomerResponse> searchCustomers(String query) {
        if (query == null || query.trim().length() < 3) {
            throw new BadRequestException("Search query must be at least 3 characters");
        }

        RetailerProfile retailer = getRetailerProfile();
        String q = query.trim();

        List<Customer> results;
        // If query looks like an email (contains @), search by email; otherwise by phone
        if (q.contains("@")) {
            results = customerRepository.searchByEmail(retailer.getId(), q);
        } else {
            results = customerRepository.searchByPhone(retailer.getId(), q);
        }

        return results.stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void sendOtp(Long customerId) {
        RetailerProfile retailer = getRetailerProfile();
        Customer customer = getCustomerForRetailer(customerId, retailer.getId());

        String otp = String.format("%06d", new Random().nextInt(999999));

        CustomerOtp customerOtp = new CustomerOtp();
        customerOtp.setCustomer(customer);
        customerOtp.setOtp(otp);
        customerOtp.setExpiresAt(LocalDateTime.now().plusMinutes(10));
        customerOtpRepository.save(customerOtp);

        // Dev mode: print to console
        System.out.println("=== CUSTOMER OTP === Phone: " + customer.getPhone() + " | OTP: " + otp + " ===");
    }

    @Override
    @Transactional
    public void verifyOtp(Long customerId, VerifyCustomerOtpRequest request) {
        RetailerProfile retailer = getRetailerProfile();
        Customer customer = getCustomerForRetailer(customerId, retailer.getId());

        CustomerOtp otp = customerOtpRepository
                .findTopByCustomer_IdAndVerifiedFalseOrderByCreatedAtDesc(customerId)
                .orElseThrow(() -> new BadRequestException("No OTP found. Please request a new one."));

        if (otp.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("OTP has expired. Please request a new one.");
        }

        if (otp.getAttempts() >= 5) {
            throw new BadRequestException("Too many failed attempts. Please request a new OTP.");
        }

        if (!otp.getOtp().equals(request.getOtp())) {
            otp.setAttempts(otp.getAttempts() + 1);
            customerOtpRepository.save(otp);
            throw new BadRequestException("Invalid OTP");
        }

        otp.setVerified(true);
        customerOtpRepository.save(otp);

        customer.setPhoneVerified(true);
        customerRepository.save(customer);
    }

    // ── Helpers ──

    private RetailerProfile getRetailerProfile() {
        String email = SecurityUtils.getCurrentUserEmail();
        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadRequestException("User not found"));
        return retailerProfileRepository.findByUser_Id(user.getId())
                .orElseThrow(() -> new BadRequestException("Retailer profile not found. Please complete retailer registration first."));
    }

    private Customer getCustomerForRetailer(Long customerId, Long retailerProfileId) {
        return customerRepository.findByIdAndRetailerProfile_Id(customerId, retailerProfileId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
    }

    private CustomerResponse toResponse(Customer c) {
        return new CustomerResponse(
                c.getId(),
                c.getUniqueId(),
                c.getName(),
                c.getPhone(),
                c.getEmail(),
                c.getPhoneVerified(),
                c.getCreatedAt()
        );
    }
}
