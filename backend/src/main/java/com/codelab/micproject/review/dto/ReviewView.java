package com.codelab.micproject.review.dto;

import java.time.OffsetDateTime;

public record ReviewView(
        Long id,
        Long reviewerId,
        Long consultantId,
        int rating,
        String comment,
        OffsetDateTime createdAt
) {}