package com.sparta.logistics.application.event;

import java.time.Instant;
import java.util.UUID;

public record HubDeletedEvent(
        UUID hubId,
        Instant deletedAt
) {
}
