package com.codelab.micproject.booking.dto;

import java.time.OffsetDateTime;

public record AppointmentView(Long id, Long consultantId, Long userId, OffsetDateTime startAt, OffsetDateTime endAt, String status, String meetingUrl){}
