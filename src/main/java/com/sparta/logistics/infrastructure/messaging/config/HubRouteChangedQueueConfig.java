package com.sparta.logistics.infrastructure.messaging.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HubRouteChangedQueueConfig {

    @Value("${message.queue.hub-route-changed}")
    private String queueName;

    @Value("${message.routing-key.hub-route-changed}")
    private String routingKey;

    @Bean
    public Queue hubRouteChangedQueue() {
        return new Queue(queueName);
    }

    @Bean
    public Binding hubRouteChangedBinding(TopicExchange exchange) {
        return BindingBuilder
                .bind(hubRouteChangedQueue())
                .to(exchange)
                .with(routingKey);
    }
}
