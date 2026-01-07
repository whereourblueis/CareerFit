package com.codelab.micproject.payment.controller;

import com.codelab.micproject.common.response.ApiResponse;
import com.codelab.micproject.payment.dto.*;
import com.codelab.micproject.payment.service.PaymentService;
import com.codelab.micproject.security.oauth2.UserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService service;


    // 1) 체크아웃 (로그인 필요)
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @PostMapping("/checkout")
    public ApiResponse<CheckoutResponse> checkout(@AuthenticationPrincipal UserPrincipal me,
                                                  @RequestBody @Valid CheckoutRequest req){
        return ApiResponse.ok(service.checkout(me, req));
    }


    // 2) PG 웹훅(시뮬레이션)
    @PostMapping("/webhook")
    public ApiResponse<Void> webhook(@RequestBody PaymentWebhook webhook){
        service.webhook(webhook);
        return ApiResponse.ok();
    }
}
