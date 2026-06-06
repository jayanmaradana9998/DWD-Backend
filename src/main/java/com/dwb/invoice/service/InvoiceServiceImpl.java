package com.dwb.invoice.service;

import com.dwb.customer.entity.Customer;
import com.dwb.customer.repository.CustomerRepository;
import com.dwb.exception.custom.BadRequestException;
import com.dwb.exception.custom.ResourceNotFoundException;
import com.dwb.invoice.dto.CreateInvoiceRequest;
import com.dwb.invoice.dto.InvoiceItemResponse;
import com.dwb.invoice.dto.InvoiceResponse;
import com.dwb.invoice.entity.Invoice;
import com.dwb.invoice.entity.InvoiceItem;
import com.dwb.invoice.repository.InvoiceRepository;
import com.dwb.retailer.entity.RetailerProfile;
import com.dwb.retailer.repository.RetailerProfileRepository;
import com.dwb.security.util.SecurityUtils;
import com.dwb.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InvoiceServiceImpl implements InvoiceService {

    private static final BigDecimal PLATFORM_CHARGE = new BigDecimal("10.00");
    private static final BigDecimal WARRANTY_CHARGE = new BigDecimal("2499.00");

    private final InvoiceRepository invoiceRepository;
    private final CustomerRepository customerRepository;
    private final RetailerProfileRepository retailerProfileRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public InvoiceResponse createInvoice(CreateInvoiceRequest request) {
        RetailerProfile retailer = getRetailerProfile();

        Customer customer = customerRepository.findByIdAndRetailerProfile_Id(request.getCustomerId(), retailer.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        Invoice invoice = new Invoice();
        invoice.setRetailerProfile(retailer);
        invoice.setCustomer(customer);

        // Build line items
        List<InvoiceItem> items = request.getItems().stream().map(req -> {
            InvoiceItem item = new InvoiceItem();
            item.setInvoice(invoice);
            item.setProductName(req.getProductName());
            item.setImei(req.getImei());
            item.setCategory(req.getCategory());
            item.setQuantity(req.getQuantity());
            item.setUnitPrice(req.getUnitPrice());
            item.setTaxAmount(req.getTaxAmount());
            return item;
        }).collect(Collectors.toList());

        invoice.setItems(items);

        // Calculate totals
        BigDecimal subtotal = items.stream()
                .map(i -> i.getUnitPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal taxTotal = items.stream()
                .map(i -> i.getTaxAmount().multiply(BigDecimal.valueOf(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal warrantyCharge = request.isIncludeWarranty() ? WARRANTY_CHARGE : BigDecimal.ZERO;
        BigDecimal total = subtotal.add(taxTotal).add(PLATFORM_CHARGE).add(warrantyCharge);

        invoice.setSubtotal(subtotal);
        invoice.setTaxTotal(taxTotal);
        invoice.setPlatformCharge(PLATFORM_CHARGE);
        invoice.setWarrantyCharge(warrantyCharge);
        invoice.setTotalAmount(total);

        // First save to get the id
        Invoice saved = invoiceRepository.save(invoice);
        saved.setInvoiceNumber("INV" + String.format("%06d", saved.getId()));
        saved = invoiceRepository.save(saved);

        return toResponse(saved);
    }

    @Override
    public List<InvoiceResponse> getInvoices() {
        RetailerProfile retailer = getRetailerProfile();
        return invoiceRepository.findByRetailerProfile_IdOrderByCreatedAtDesc(retailer.getId())
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    public InvoiceResponse getInvoice(Long id) {
        RetailerProfile retailer = getRetailerProfile();
        Invoice invoice = invoiceRepository.findByIdAndRetailerProfile_Id(id, retailer.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found"));
        return toResponse(invoice);
    }

    // ── Helpers ──

    private RetailerProfile getRetailerProfile() {
        String email = SecurityUtils.getCurrentUserEmail();
        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadRequestException("User not found"));
        return retailerProfileRepository.findByUser_Id(user.getId())
                .orElseThrow(() -> new BadRequestException("Retailer profile not found"));
    }

    private InvoiceResponse toResponse(Invoice inv) {
        List<InvoiceItemResponse> itemResponses = inv.getItems().stream().map(i ->
                new InvoiceItemResponse(
                        i.getId(),
                        i.getProductName(),
                        i.getImei(),
                        i.getCategory(),
                        i.getQuantity(),
                        i.getUnitPrice(),
                        i.getTaxAmount(),
                        i.getUnitPrice().add(i.getTaxAmount()).multiply(BigDecimal.valueOf(i.getQuantity()))
                )
        ).collect(Collectors.toList());

        return new InvoiceResponse(
                inv.getId(),
                inv.getInvoiceNumber(),
                inv.getCustomer().getId(),
                inv.getCustomer().getName(),
                inv.getCustomer().getPhone(),
                inv.getSubtotal(),
                inv.getTaxTotal(),
                inv.getPlatformCharge(),
                inv.getWarrantyCharge(),
                inv.getTotalAmount(),
                inv.getStatus(),
                itemResponses,
                inv.getCreatedAt()
        );
    }
}
