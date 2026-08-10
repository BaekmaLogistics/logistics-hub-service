package com.sparta.logistics.infrastructure.messaging.publisher;

import com.sparta.logistics.application.event.HubDeletedEvent;
import com.sparta.logistics.application.event.HubRouteChangedIntegrationEvent;
import com.sparta.logistics.application.event.InventoryLowEvent;
import com.sparta.logistics.application.port.IntegrationEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RabbitIntegrationEventPublisher implements IntegrationEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Override
    public void publish(InventoryLowEvent event){

    }

    @Override
    public void publish(HubDeletedEvent hubDeletedEvent){

    }

    @Override
    public void publish(HubRouteChangedIntegrationEvent event){

    }
}
