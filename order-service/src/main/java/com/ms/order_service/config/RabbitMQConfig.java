package com.ms.order_service.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(MessagingProperties.class)
public class RabbitMQConfig {

    @Bean
    public Queue queue(MessagingProperties properties) {
        return new Queue(properties.queueName(), true);
    }

    @Bean
    public TopicExchange exchange(MessagingProperties properties) {
        return new TopicExchange(properties.exchangeName());
    }

    @Bean
    public Binding binding(Queue queue, TopicExchange exchange, MessagingProperties properties) {
        return BindingBuilder.bind(queue).to(exchange).with(properties.bindingRoutingKey());
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new JacksonJsonMessageConverter();
    }
}
