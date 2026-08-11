package com.sparta.logistics.infrastructure.messaging.consumer;

import com.sparta.logistics.application.event.HubRouteChangeType;
import com.sparta.logistics.application.event.HubRouteChangedIntegrationEvent;
import com.sparta.logistics.application.graph.HubGraphManager;
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

import static org.mockito.Mockito.verify;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
class HubRouteChangedConsumerTest {

    @Mock
    private HubGraphManager hubGraphManager;

    @InjectMocks
    private HubRouteChangedConsumer hubRouteChangedConsumer;

    @Test
    @DisplayName("허브 경로 변경 이벤트를 받으면 이벤트의 그래프 버전으로 그래프를 재적재한다")
    void consumeHubRouteChangedEvent() {
        // given
        long graphVersion = 3L;

        HubRouteChangedIntegrationEvent event =
                new HubRouteChangedIntegrationEvent(
                        UUID.randomUUID(),
                        HubRouteChangeType.UPDATED,
                        Instant.now(),
                        graphVersion
                );

        EventEnvelope<HubRouteChangedIntegrationEvent> envelope =
                EventEnvelope.of(
                        "HubRouteChanged",
                        event,
                        UUID.randomUUID()
                );

        // when
        hubRouteChangedConsumer.consume(envelope);

        // then
        verify(hubGraphManager).reloadGraph(graphVersion);
    }
}