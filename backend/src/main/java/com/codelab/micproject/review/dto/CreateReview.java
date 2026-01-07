package com.codelab.micproject.review.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateReview(
        @NotNull Long consultantId,
        @Min(1) @Max(5) int rating,
        @Size(max = 1000) String comment
) {}