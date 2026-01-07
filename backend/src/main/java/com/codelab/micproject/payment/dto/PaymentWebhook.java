package com.codelab.micproject.payment.dto;

public record PaymentWebhook(Long orderId, String status, String pgTransactionId) {}
