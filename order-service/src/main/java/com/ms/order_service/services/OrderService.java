package com.ms.order_service.services;

import com.ms.order_service.dtos.ShowReserveRequestDto;
import com.ms.order_service.dtos.ShowResponseDto;
import com.ms.order_service.repositories.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final CatalogServiceClient catalogServiceClient;

    public void createOrder(UUID showId, ShowReserveRequestDto showReserveRequestDto) {
        ShowResponseDto response = catalogServiceClient.reserveShowTickets(showId, showReserveRequestDto.quantity());

        log.info("Show response: {}", response.name());
    }

    public void publishOrderCreatedEvent() {

    }
}
