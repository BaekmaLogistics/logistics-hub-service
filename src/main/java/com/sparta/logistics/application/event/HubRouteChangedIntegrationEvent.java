package com.sparta.logistics.application.event;

import java.time.Instant;
import java.util.UUID;

public record HubRouteChangedIntegrationEvent(
        UUID routeId,
        HubRouteChangeType changeType,
        Instant occurredAt
) {
}
