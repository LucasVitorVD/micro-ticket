package com.ms.order_service.dtos;

import java.util.UUID;

public record ShowReserveRequestDto(
        UUID showId,
        int quantity
) {}
