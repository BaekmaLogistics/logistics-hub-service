package com.sparta.logistics.application.event;

import java.time.Instant;
import java.util.UUID;

public record HubRouteChangedIntegrationEvent(
        UUID actorId,
        UUID routeId,
        HubRouteChangeType changeType,
        Instant occurredAt
) {
}
