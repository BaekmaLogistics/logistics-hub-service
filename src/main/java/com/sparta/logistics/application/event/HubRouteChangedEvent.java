//HubRoute가 변경됐어!
//commit 성공하면 버전 올리고 RabbitMQ로 알려줘!
package com.sparta.logistics.application.event;

import java.time.Instant;
import java.util.UUID;

public record HubRouteChangedEvent(
        UUID routeId,
        HubRouteChangeType changeType,
        Instant occurredAt
) {
}
