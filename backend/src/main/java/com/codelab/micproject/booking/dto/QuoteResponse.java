package com.codelab.micproject.booking.dto;

public record QuoteResponse(Long consultantId, String level, int bundleCount, java.math.BigDecimal unitPrice, java.math.BigDecimal totalPrice){}
