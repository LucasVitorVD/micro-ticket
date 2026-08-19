package com.ms.order_service.services;

import com.ms.order_service.config.MessagingProperties;
import com.ms.order_service.dtos.ShowResponseDto;
import com.ms.order_service.entities.Order;
import com.ms.order_service.enums.OrderStatus;
import com.ms.order_service.repositories.OrderRepository;
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
    private final OrderRepository orderRepository;
    private final RabbitTemplate rabbitTemplate;
    private final MessagingProperties messagingProperties;

    public void createOrder(UUID showId, int ticketQuantity, String customerId, String customerEmail) {
        ShowResponseDto response = catalogServiceClient.reserveShowTickets(showId, ticketQuantity);

        log.info("Show response: {}", response.name());

        Order order = new Order();
        order.setCustomerId(customerId);
        order.setShowId(showId);
        order.setCustomerEmail(customerEmail);
        order.setTicketQuantity(ticketQuantity);
        order.setTotalAmount(response.price() * ticketQuantity);
        order.setStatus(OrderStatus.CONFIRMED);
        orderRepository.save(order);

        publishOrderCreatedEvent(response);
    }

    private void publishOrderCreatedEvent(ShowResponseDto message) {
        rabbitTemplate.convertAndSend(messagingProperties.exchangeName(), messagingProperties.orderCreatedRoutingKey(), message);
    }
}
