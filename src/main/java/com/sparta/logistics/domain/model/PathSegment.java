package com.sparta.logistics.domain.model;

import java.util.UUID;

public record PathSegment(
        UUID fromHubId,
        UUID toHubId,
        double distance,
        int duration
        ) {
}
