package com.ms.notification_service.consumers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MessageConsumer {

    @RabbitListener(queues = "${app.messaging.queue-name}")
    public void receiveMessage(String message) {
        log.info("Received message from queue: {}", message);
    }
}