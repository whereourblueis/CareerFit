package com.codelab.micproject.payment.dto;

import java.math.BigDecimal;

public record CheckoutResponse(Long orderId, BigDecimal totalPrice, String paymentUrl) {}
