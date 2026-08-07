package com.sparta.logistics.application.port;

import com.sparta.logistics.domain.model.ShortestPath;

import java.util.Optional;
import java.util.UUID;

public interface ShortestPathCache {

    Optional<ShortestPath> get(
            UUID fromHubId,
            UUID toHubId
    );

    void put(
            UUID fromHubId,
            UUID toHubId,
            ShortestPath shortestPath
    );

    void evictAll();
}
