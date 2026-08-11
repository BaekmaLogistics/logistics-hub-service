package com.sparta.logistics.application.port;

import com.sparta.logistics.domain.model.ShortestPath;

import java.util.Optional;
import java.util.UUID;

public interface ShortestPathCache {

    Optional<ShortestPath> get(
            long graphVersion,
            UUID fromHubId,
            UUID toHubId
    );

    void put(
            long graphVersion,
            UUID fromHubId,
            UUID toHubId,
            ShortestPath shortestPath
    );
}
