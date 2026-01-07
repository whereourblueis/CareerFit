package com.codelab.micproject.payment.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

public record OrderView(
        Long id,
        Long consultantId,
        String consultantName,
        int bundleCount,
        BigDecimal unitPrice,
        BigDecimal totalPrice,
        String status,
        OffsetDateTime createdAt,
        List<OrderView.AppointmentSummary> appointments
) {
    public record AppointmentSummary(
            Long id,
            OffsetDateTime startAt,
            OffsetDateTime endAt,
            String status,
            String meetingUrl
    ) {}
}
