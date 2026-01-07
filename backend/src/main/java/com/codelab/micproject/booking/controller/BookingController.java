package com.codelab.micproject.booking.controller;


import com.codelab.micproject.booking.dto.*;
import com.codelab.micproject.booking.service.BookingService;
import com.codelab.micproject.common.response.ApiResponse;
import com.codelab.micproject.security.oauth2.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


import java.util.List;


@RestController @RequestMapping("/api/booking") @RequiredArgsConstructor
public class BookingController {
    private final BookingService service;


    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @PostMapping("/request")
    public ApiResponse<AppointmentView> request(@AuthenticationPrincipal UserPrincipal me, @RequestBody RequestAppointment req){
        return ApiResponse.ok(service.request(me, req));
    }


    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @PostMapping("/request-batch")
    public ApiResponse<List<AppointmentView>> requestBatch(@AuthenticationPrincipal UserPrincipal me, @RequestBody RequestAppointmentBatch req){
        return ApiResponse.ok(service.requestBatch(me, req));
    }


    @PreAuthorize("hasRole('CONSULTANT')")
    @PostMapping("/{id}/approve")
    public ApiResponse<AppointmentView> approve(@AuthenticationPrincipal UserPrincipal me, @PathVariable Long id){
        return ApiResponse.ok(service.approve(me, id));
    }


    // 결제 전 견적 미리보기 (공개)
    @GetMapping("/quote")
    public ApiResponse<QuoteResponse> quote(@RequestParam Long consultantId, @RequestParam SessionBundle bundle){
        return ApiResponse.ok(service.quote(consultantId, bundle));
    }
}