package com.ms.order_service.services;

import com.ms.order_service.config.RabbitMQConfig;
import com.ms.order_service.dtos.ShowResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {
    private final CatalogServiceClient catalogServiceClient;
    private final RabbitTemplate rabbitTemplate;

    public void createOrder(UUID showId, int ticketQuantity) {
        ShowResponseDto response = catalogServiceClient.reserveShowTickets(showId, ticketQuantity);

        log.info("Show response: {}", response.name());

        publishOrderCreatedEvent(response);
    }

    private void publishOrderCreatedEvent(ShowResponseDto message) {
        String specificRoutingKey = "order.routing.createOrder";
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME, specificRoutingKey, message);
    }
}
