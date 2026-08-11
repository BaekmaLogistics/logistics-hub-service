package com.sparta.logistics.infrastructure.messaging.consumer;

import com.sparta.logistics.application.event.HubRouteChangedIntegrationEvent;
import com.sparta.logistics.application.graph.HubGraphManager;
import com.sparta.logistics.application.port.GraphVersionStore;
import com.sparta.logistics.application.port.ShortestPathCache;
import com.sparta.logistics.infrastructure.messaging.envelope.EventEnvelope;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class HubRouteChangedConsumer {

    private final HubGraphManager hubGraphManager;


    @RabbitListener(queues = "${message.queue.hub-route-changed}")
    public void consume(EventEnvelope<HubRouteChangedIntegrationEvent> envelope){

        HubRouteChangedIntegrationEvent event = envelope.payload();
        log.info("허브 경로 변경 메시지 수신: {}", event);

        hubGraphManager.reloadGraph(event.graphVersion());

        log.info("허브 그래프 재적재 및 최단 경로 캐시 초기화 완료");
    }
}
