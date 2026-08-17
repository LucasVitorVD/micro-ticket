package com.ms.order_service.services;

import com.ms.order_service.dtos.ShowReserveRequestDto;
import com.ms.order_service.dtos.ShowResponseDto;
import com.ms.order_service.exceptions.ResourceNotFoundException;
import com.ms.order_service.exceptions.TicketsUnavailableException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.UUID;

@Service
public class CatalogServiceClient {
    private final RestClient restClient;

    public CatalogServiceClient(RestClient.Builder builder, @Value("${app.catalog-service.base-url}") String catalogServiceBaseUrl) {
        this.restClient = builder.baseUrl(catalogServiceBaseUrl).build();
    }

    public ShowResponseDto reserveShowTickets(UUID showId, int quantity) {
        return restClient.post()
                .uri("/api/v1/show/{showId}/reserve", showId)
                .body(new ShowReserveRequestDto(showId, quantity))
                .retrieve()
                .onStatus(status -> status.equals(HttpStatus.CONFLICT), (request, response) -> {
                    throw new TicketsUnavailableException("There aren't enough tickets available for show " + showId);
                })
                .onStatus(status -> status.equals(HttpStatus.NOT_FOUND), (request, response) -> {
                    throw new ResourceNotFoundException("Show not found: " + showId);
                })
                .body(ShowResponseDto.class);
    }
}
