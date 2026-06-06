package com.dwb.warranty.service;

import com.dwb.warranty.dto.CreateWarrantyRequest;
import com.dwb.warranty.dto.WarrantyResponse;

import java.util.List;

public interface WarrantyService {

    WarrantyResponse createWarranty(CreateWarrantyRequest request);

    List<WarrantyResponse> getWarranties();

    WarrantyResponse getWarranty(Long id);
}
