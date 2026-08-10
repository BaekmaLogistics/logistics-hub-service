package com.sparta.logistics.infrastructure.messaging.publisher;

import com.sparta.logistics.application.event.HubDeletedEvent;
import com.sparta.logistics.application.event.HubRouteChangedIntegrationEvent;
import com.sparta.logistics.application.event.InventoryLowEvent;
import com.sparta.logistics.application.port.CurrentUserProvider;
import com.sparta.logistics.application.port.IntegrationEventPublisher;
import com.sparta.logistics.infrastructure.messaging.envelope.EventEnvelope;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RabbitIntegrationEventPublisher implements IntegrationEventPublisher {

    @Value("${message.exchange}")
    private String exchange;

    @Value(("${message.routing-key.hub-route-changed}"))
    private String hubRouteChangedRoutingKey;

    @Value("${message.routing-key.inventory-low}")
    private String inventoryLowRoutingKey;

    @Value("${message.routing-key.hub-deleted}")
    private String hubDeletedRoutingKey;

    private final RabbitTemplate rabbitTemplate;
    private final CurrentUserProvider currentUserProvider;

    @Override
    public void publish(InventoryLowEvent event){
        EventEnvelope<InventoryLowEvent> envelope = EventEnvelope.of(
                "InventoryLow",
                event,
                currentUserProvider.getCurrentUserId()
        );

        rabbitTemplate.convertAndSend(
                exchange,
                inventoryLowRoutingKey,
                envelope
        );
    }

    @Override
    public void publish(HubDeletedEvent event){
        EventEnvelope<HubDeletedEvent> envelope = EventEnvelope.of(
                "HubDeleted",
                event,
                currentUserProvider.getCurrentUserId()
        );

        log.info("레빗 발행: exchange={}, routingkey={}, envelope={}", exchange, hubDeletedRoutingKey, envelope);

        rabbitTemplate.convertAndSend(
                exchange,
                hubDeletedRoutingKey,
                envelope
        );
    }

    @Override
    public void publish(HubRouteChangedIntegrationEvent event){
        EventEnvelope<HubRouteChangedIntegrationEvent> envelope = EventEnvelope.of(
                "HubRouteChanged",
                event,
                currentUserProvider.getCurrentUserId()
        );

        rabbitTemplate.convertAndSend(
                exchange,
                hubRouteChangedRoutingKey,
                envelope
        );
    }
}
