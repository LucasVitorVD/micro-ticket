package com.ms.order_service.services;

import com.ms.order_service.config.MessagingProperties;
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
    private final MessagingProperties messagingProperties;

    public void createOrder(UUID showId, int ticketQuantity) {
        ShowResponseDto response = catalogServiceClient.reserveShowTickets(showId, ticketQuantity);

        log.info("Show response: {}", response.name());

        publishOrderCreatedEvent(response);
    }

    private void publishOrderCreatedEvent(ShowResponseDto message) {
        rabbitTemplate.convertAndSend(messagingProperties.exchangeName(), messagingProperties.orderCreatedRoutingKey(), message);
    }
}
