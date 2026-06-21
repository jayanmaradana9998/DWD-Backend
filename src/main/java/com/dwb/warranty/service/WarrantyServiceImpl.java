package com.dwb.warranty.service;

import com.dwb.exception.custom.BadRequestException;
import com.dwb.exception.custom.ResourceNotFoundException;
import com.dwb.invoice.entity.Invoice;
import com.dwb.invoice.repository.InvoiceRepository;
import com.dwb.retailer.entity.RetailerProfile;
import com.dwb.retailer.repository.RetailerProfileRepository;
import com.dwb.security.util.SecurityUtils;
import com.dwb.user.repository.UserRepository;
import com.dwb.warranty.dto.CreateWarrantyRequest;
import com.dwb.warranty.dto.WarrantyResponse;
import com.dwb.warranty.entity.Warranty;
import com.dwb.warranty.repository.WarrantyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WarrantyServiceImpl implements WarrantyService {

    private final WarrantyRepository warrantyRepository;
    private final InvoiceRepository invoiceRepository;
    private final RetailerProfileRepository retailerProfileRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public WarrantyResponse createWarranty(CreateWarrantyRequest request) {
        RetailerProfile retailer = getRetailerProfile();

        Invoice invoice = invoiceRepository.findByIdAndRetailerProfile_Id(request.getInvoiceId(), retailer.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found"));

        if (warrantyRepository.existsByInvoice_Id(invoice.getId())) {
            throw new BadRequestException("A warranty already exists for this invoice");
        }

        Warranty warranty = new Warranty();
        warranty.setInvoice(invoice);
        warranty.setCustomer(invoice.getCustomer());
        warranty.setRetailerProfile(retailer);
        warranty.setSkipped(request.isSkipped());

        if (!request.isSkipped()) {
            if (request.getWarrantyType() == null) {
                throw new BadRequestException("Warranty type is required");
            }
            if (request.getDurationMonths() == null || request.getDurationMonths() < 1) {
                throw new BadRequestException("Duration in months is required");
            }

            LocalDate start = LocalDate.now();
            warranty.setWarrantyType(request.getWarrantyType());
            warranty.setProvider(request.getProvider());
            warranty.setDurationMonths(request.getDurationMonths());
            warranty.setStartDate(start);
            warranty.setEndDate(start.plusMonths(request.getDurationMonths()));
            warranty.setCoverageSummary(request.getCoverageSummary());
            warranty.setIsExtended(request.getIsExtended() != null && request.getIsExtended());
        } else {
            // Skipped — store placeholder dates
            warranty.setWarrantyType(null);
            warranty.setDurationMonths(0);
            warranty.setStartDate(LocalDate.now());
            warranty.setEndDate(LocalDate.now());
        }

        Warranty saved = warrantyRepository.save(warranty);
        saved.setWarrantyNumber("WAR" + String.format("%06d", saved.getId()));
        saved = warrantyRepository.save(saved);

        return toResponse(saved);
    }

    @Override
    public List<WarrantyResponse> getWarranties() {
        RetailerProfile retailer = getRetailerProfile();
        return warrantyRepository.findByRetailerProfile_IdOrderByCreatedAtDesc(retailer.getId())
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    public WarrantyResponse getWarranty(Long id) {
        RetailerProfile retailer = getRetailerProfile();
        Warranty warranty = warrantyRepository.findByIdAndRetailerProfile_Id(id, retailer.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Warranty not found"));
        return toResponse(warranty);
    }

    // ── Helpers ──

    private RetailerProfile getRetailerProfile() {
        String email = SecurityUtils.getCurrentUserEmail();
        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadRequestException("User not found"));
        return retailerProfileRepository.findByUser_Id(user.getId())
                .orElseThrow(() -> new BadRequestException("Retailer profile not found"));
    }

    private WarrantyResponse toResponse(Warranty w) {
        return new WarrantyResponse(
                w.getId(),
                w.getWarrantyNumber(),
                w.getInvoice().getId(),
                w.getInvoice().getInvoiceNumber(),
                w.getCustomer().getId(),
                w.getCustomer().getName(),
                w.getWarrantyType(),
                w.getProvider(),
                w.getDurationMonths(),
                w.getStartDate(),
                w.getEndDate(),
                w.getCoverageSummary(),
                w.getIsExtended(),
                w.getSkipped(),
                w.getCreatedAt()
        );
    }
}
