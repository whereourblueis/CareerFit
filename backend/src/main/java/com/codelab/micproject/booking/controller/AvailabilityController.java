package com.codelab.micproject.booking.controller;


import com.codelab.micproject.booking.dto.*;
import com.codelab.micproject.booking.service.AvailabilityService;
import com.codelab.micproject.common.response.ApiResponse;
import com.codelab.micproject.security.oauth2.UserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


import java.time.LocalDate;
import java.util.List;


@RestController @RequestMapping("/api/availability") @RequiredArgsConstructor
public class AvailabilityController {
    private final AvailabilityService service;


    // 컨설턴트가 자신의 가용 시간 추가
    @PreAuthorize("hasRole('CONSULTANT')")
    @PostMapping
    public ApiResponse<AvailabilityView> upsert(@AuthenticationPrincipal UserPrincipal me,
                                                @RequestBody @Valid UpsertAvailabilityReq req){
        return ApiResponse.ok(service.upsert(me, req));
    }


    // 공개: 특정 컨설턴트의 기간 내 슬롯 생성 결과 조회
    @GetMapping("/{consultantId}/slots")
    public ApiResponse<List<SlotDto>> slots(@PathVariable Long consultantId,
                                            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
                                            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to){

        // B) 날짜 파라미터 검증 (to가 from보다 이전이면 에러)
        if (to.isBefore(from)) {
            throw new IllegalArgumentException("`to` must be the same or after `from`");
        }

        // C) 범위 제한 (예: 최대 60일)
        long days = java.time.temporal.ChronoUnit.DAYS.between(from, to) + 1; // 양 끝 포함 느낌이면 +1
        if (days > 60) {
            throw new IllegalArgumentException("Date range is too wide (max 60 days)");
        }

        return ApiResponse.ok(service.generateSlots(consultantId, from, to));
    }
}