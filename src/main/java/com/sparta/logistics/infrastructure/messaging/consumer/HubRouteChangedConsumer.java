package com.sparta.logistics.infrastructure.messaging.consumer;

import com.sparta.logistics.application.event.HubRouteChangedIntegrationEvent;
import com.sparta.logistics.application.graph.HubGraphManager;
import com.sparta.logistics.application.port.ShortestPathCache;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class HubRouteChangedConsumer {

    private final HubGraphManager hubGraphManager;
    private final ShortestPathCache shortestPathCache;

    @RabbitListener(queues = "${message.queue.hub}")
    public void consume(HubRouteChangedIntegrationEvent event){
        hubGraphManager.reloadGraph();
        shortestPathCache.evictAll();
    }
}
