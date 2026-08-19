package com.ms.order_service.controllers;

import com.ms.order_service.config.Auth0Properties;
import com.ms.order_service.dtos.ShowReserveRequestDto;
import com.ms.order_service.services.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/order")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    private final Auth0Properties auth0Properties;

    @PostMapping
    public ResponseEntity<Void> createNewOrder(
            @RequestBody ShowReserveRequestDto showReserveRequestDto,
            @AuthenticationPrincipal Jwt jwt
    ) {
        orderService.createOrder(
                showReserveRequestDto.showId(),
                showReserveRequestDto.quantity(),
                jwt.getSubject(),
                jwt.getClaimAsString(auth0Properties.emailClaim())
        );

        return ResponseEntity.ok().build();
    }
}
