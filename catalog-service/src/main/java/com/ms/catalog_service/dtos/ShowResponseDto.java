package com.ms.catalog_service.dtos;

import java.util.UUID;

public record ShowResponseDto(
        UUID id,
        String name,
        String description,
        double price,
        int totalTickets,
        int availableTickets
) {}