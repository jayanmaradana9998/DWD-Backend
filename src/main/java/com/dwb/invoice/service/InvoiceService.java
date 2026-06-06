package com.dwb.invoice.service;

import com.dwb.invoice.dto.CreateInvoiceRequest;
import com.dwb.invoice.dto.InvoiceResponse;

import java.util.List;

public interface InvoiceService {

    InvoiceResponse createInvoice(CreateInvoiceRequest request);

    List<InvoiceResponse> getInvoices();

    InvoiceResponse getInvoice(Long id);
}
