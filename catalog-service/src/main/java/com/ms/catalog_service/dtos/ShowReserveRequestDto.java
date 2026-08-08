package com.ms.catalog_service.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;

public record ShowReserveRequestDto(
        @Positive
        @Min(1)
        int quantity
) {
}
