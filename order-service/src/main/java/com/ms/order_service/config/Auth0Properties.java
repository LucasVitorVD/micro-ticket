package com.ms.order_service.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.auth0")
public record Auth0Properties(String emailClaim) {}
