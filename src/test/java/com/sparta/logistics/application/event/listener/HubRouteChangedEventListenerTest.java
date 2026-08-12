package com.sparta.logistics.application.event.listener;

import com.sparta.logistics.application.event.HubRouteChangeType;
import com.sparta.logistics.application.event.HubRouteChangedEvent;
import com.sparta.logistics.application.event.HubRouteChangedIntegrationEvent;
import com.sparta.logistics.application.port.GraphVersionStore;
import com.sparta.logistics.application.port.IntegrationEventPublisher;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
class HubRouteChangedEventListenerTest {

    @Mock
    private GraphVersionStore graphVersionStore;

    @Mock
    private IntegrationEventPublisher integrationEventPublisher;

    @InjectMocks
    private HubRouteChangedEventListener listener;

    @Test
    @DisplayName("허브 경로 변경 시 그래프 버전을 증가시키고 증가된 버전으로 통합 이벤트를 발행한다")
    void handle_success() {
        // given
        UUID routeId = UUID.randomUUID();
        Instant occurredAt = Instant.now();

        HubRouteChangedEvent event =
                new HubRouteChangedEvent(
                        routeId,
                        HubRouteChangeType.UPDATED,
                        occurredAt
                );

        long newGraphVersion = 3L;

        given(graphVersionStore.increment())
                .willReturn(newGraphVersion);

        // when
        listener.handle(event);

        // then
        verify(graphVersionStore).increment();

        ArgumentCaptor<HubRouteChangedIntegrationEvent> captor =
                ArgumentCaptor.forClass(
                        HubRouteChangedIntegrationEvent.class
                );

        verify(integrationEventPublisher)
                .publish(captor.capture());

        HubRouteChangedIntegrationEvent publishedEvent =
                captor.getValue();

        assertThat(publishedEvent.routeId())
                .isEqualTo(routeId);

        assertThat(publishedEvent.changeType())
                .isEqualTo(HubRouteChangeType.UPDATED);

        assertThat(publishedEvent.occurredAt())
                .isEqualTo(occurredAt);

        assertThat(publishedEvent.graphVersion())
                .isEqualTo(newGraphVersion);
    }
}