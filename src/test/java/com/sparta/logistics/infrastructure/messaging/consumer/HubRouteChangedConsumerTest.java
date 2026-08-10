package com.sparta.logistics.infrastructure.messaging.consumer;

import com.sparta.logistics.application.event.HubRouteChangeType;
import com.sparta.logistics.application.event.HubRouteChangedIntegrationEvent;
import com.sparta.logistics.application.graph.HubGraphManager;
import com.sparta.logistics.application.port.GraphVersionStore;
import com.sparta.logistics.application.port.ShortestPathCache;
import com.sparta.logistics.infrastructure.messaging.envelope.EventEnvelope;
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
import static org.mockito.Mockito.when;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
class HubRouteChangedConsumerTest {

    @Mock
    private HubGraphManager hubGraphManager;

    @Mock
    private ShortestPathCache shortestPathCache;

    @Mock
    private GraphVersionStore graphVersionStore;

    @InjectMocks
    private HubRouteChangedConsumer hubRouteChangedConsumer;

    @Test
    @DisplayName("허브 경로 변경 이벤트를 받으면 최신 그래프 버전으로 재적재하고 최단 경로 캐시를 삭제한다")
    void consumeHubRouteChangedEvent() {
        // given
        long sharedVersion = 3L;

        HubRouteChangedIntegrationEvent event =
                new HubRouteChangedIntegrationEvent(
                        UUID.randomUUID(),
                        HubRouteChangeType.UPDATED,
                        Instant.now()
                );

        EventEnvelope<HubRouteChangedIntegrationEvent> envelope =
                EventEnvelope.of(
                        "HubRouteChanged",
                        event,
                        UUID.randomUUID()
                );

        when(graphVersionStore.getCurrentVersion())
                .thenReturn(sharedVersion);

        // when
        hubRouteChangedConsumer.consume(envelope);

        // then
        verify(graphVersionStore).getCurrentVersion();
        verify(hubGraphManager).reloadGraph(sharedVersion);
        verify(shortestPathCache).evictAll();
    }

}