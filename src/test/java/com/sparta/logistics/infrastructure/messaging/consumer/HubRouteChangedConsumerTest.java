package com.sparta.logistics.infrastructure.messaging.consumer;

import com.sparta.logistics.application.event.HubRouteChangeType;
import com.sparta.logistics.application.event.HubRouteChangedIntegrationEvent;
import com.sparta.logistics.application.graph.HubGraphManager;
import com.sparta.logistics.application.port.ShortestPathCache;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
class HubRouteChangedConsumerTest {

    @Mock
    private HubGraphManager hubGraphManager;

    @Mock
    private ShortestPathCache shortestPathCache;

    @InjectMocks
    private HubRouteChangedConsumer hubRouteChangedConsumer;

    @Test
    @DisplayName("허브 경로 변경 이벤트를 받으면 그래프를 재적재하고 최단 경로 캐시를 삭제한다")
    void consumeHubRouteChangedEvent(){
        HubRouteChangedIntegrationEvent event = new HubRouteChangedIntegrationEvent(
                null, //TODO Security 추가 후 수정 예정
                UUID.randomUUID(),
                HubRouteChangeType.UPDATED,
                Instant.now()
        );

        hubRouteChangedConsumer.consume(event);

        verify(hubGraphManager).reloadGraph();
        verify(shortestPathCache).evictAll();
    }

}