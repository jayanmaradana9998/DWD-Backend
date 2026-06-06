package com.dwb.warranty.controller;

import com.dwb.common.dto.BaseResponse;
import com.dwb.warranty.dto.CreateWarrantyRequest;
import com.dwb.warranty.dto.WarrantyResponse;
import com.dwb.warranty.service.WarrantyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/warranties")
@RequiredArgsConstructor
@Tag(name = "Warranties", description = "Warranty management for retailers")
public class WarrantyController {

    private final WarrantyService warrantyService;

    @PostMapping
    @Operation(summary = "Create warranty linked to an invoice (or record a skip)")
    public ResponseEntity<BaseResponse<WarrantyResponse>> createWarranty(
            @Valid @RequestBody CreateWarrantyRequest request) {

        WarrantyResponse warranty = warrantyService.createWarranty(request);
        return ResponseEntity.ok(new BaseResponse<>(true, "Warranty saved successfully", warranty));
    }

    @GetMapping
    @Operation(summary = "List all warranties for the logged-in retailer")
    public ResponseEntity<BaseResponse<List<WarrantyResponse>>> getWarranties() {
        return ResponseEntity.ok(new BaseResponse<>(true, "Warranties fetched", warrantyService.getWarranties()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get single warranty detail")
    public ResponseEntity<BaseResponse<WarrantyResponse>> getWarranty(@PathVariable Long id) {
        return ResponseEntity.ok(new BaseResponse<>(true, "Warranty fetched", warrantyService.getWarranty(id)));
    }
}
