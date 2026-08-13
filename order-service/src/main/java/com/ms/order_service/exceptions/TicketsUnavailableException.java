package com.ms.order_service.exceptions;

public class TicketsUnavailableException extends RuntimeException {
    public TicketsUnavailableException(String message) {
        super(message);
    }
}
