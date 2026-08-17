package com.ms.order_service.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.messaging")
public record MessagingProperties(
        String exchangeName,
        String queueName,
        String bindingRoutingKey,
        String orderCreatedRoutingKey
) {}
