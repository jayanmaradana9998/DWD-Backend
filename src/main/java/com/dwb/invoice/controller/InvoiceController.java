package com.dwb.invoice.controller;

import com.dwb.common.dto.BaseResponse;
import com.dwb.invoice.dto.CreateInvoiceRequest;
import com.dwb.invoice.dto.InvoiceResponse;
import com.dwb.invoice.service.InvoiceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/invoices")
@RequiredArgsConstructor
@Tag(name = "Invoices", description = "Invoice management for retailers")
public class InvoiceController {

    private final InvoiceService invoiceService;

    @PostMapping
    @Operation(summary = "Create invoice with line items")
    public ResponseEntity<BaseResponse<InvoiceResponse>> createInvoice(
            @Valid @RequestBody CreateInvoiceRequest request) {

        InvoiceResponse invoice = invoiceService.createInvoice(request);
        return ResponseEntity.ok(new BaseResponse<>(true, "Invoice created successfully", invoice));
    }

    @GetMapping
    @Operation(summary = "List all invoices for the logged-in retailer")
    public ResponseEntity<BaseResponse<List<InvoiceResponse>>> getInvoices() {
        return ResponseEntity.ok(new BaseResponse<>(true, "Invoices fetched", invoiceService.getInvoices()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get single invoice detail")
    public ResponseEntity<BaseResponse<InvoiceResponse>> getInvoice(@PathVariable Long id) {
        return ResponseEntity.ok(new BaseResponse<>(true, "Invoice fetched", invoiceService.getInvoice(id)));
    }
}
