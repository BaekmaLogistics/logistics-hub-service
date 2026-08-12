package com.sparta.logistics.domain.model;

import java.util.List;
import java.util.UUID;

public record ShortestPath(
        List<UUID> path,

        List<PathSegment> segments,

        double totalDistance,

        int totalDuration
) {
}
