//그래프 버전이 바꼈어!
//RabbitMQ로 다른 Hub 서버들에게 새 버전 알려줘!
package com.sparta.logistics.application.event;

import com.sparta.logistics.application.event.HubRouteChangeType;

import java.time.Instant;
import java.util.UUID;

public record HubRouteChangedIntegrationEvent(
        UUID routeId,
        HubRouteChangeType changeType,
        Instant occurredAt,
        long graphVersion
) {
}
