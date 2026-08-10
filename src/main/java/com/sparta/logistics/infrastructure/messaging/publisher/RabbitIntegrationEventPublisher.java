package com.sparta.logistics.infrastructure.messaging.publisher;

import com.sparta.logistics.application.event.HubDeletedEvent;
import com.sparta.logistics.application.event.HubRouteChangedIntegrationEvent;
import com.sparta.logistics.application.event.InventoryLowEvent;
import com.sparta.logistics.application.port.IntegrationEventPublisher;
import com.sparta.logistics.infrastructure.messaging.envelope.EventEnvelope;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RabbitIntegrationEventPublisher implements IntegrationEventPublisher {

    @Value("${message.exchange}")
    private String exchange;

    // TODO 메시징 공통 설정 반영 후 property로 변경
    private static final String HUB_ROUTE_CHANGED_ROUTING_KEY =
            "hub.route.changed";

    private final RabbitTemplate rabbitTemplate;

    @Override
    public void publish(InventoryLowEvent event){

    }

    @Override
    public void publish(HubDeletedEvent hubDeletedEvent){

    }

    @Override
    public void publish(HubRouteChangedIntegrationEvent event){
        EventEnvelope<HubRouteChangedIntegrationEvent> envelope = EventEnvelope.of(
                "HubRouteChanged",
                event,
                null //TODO: Security 적용 후 현재 사용자 ID 전달
        );

        rabbitTemplate.convertAndSend(
                exchange,
                HUB_ROUTE_CHANGED_ROUTING_KEY,
                envelope
        );
    }
}
