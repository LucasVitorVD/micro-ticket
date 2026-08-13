package com.ms.order_service.controllers;

import com.ms.order_service.dtos.ShowReserveRequestDto;
import com.ms.order_service.services.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/order")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<Void> createNewOrder(@RequestBody ShowReserveRequestDto showReserveRequestDto) {
        orderService.createOrder(showReserveRequestDto.showId(), showReserveRequestDto.quantity());

        return ResponseEntity.ok().build();
    }
}
