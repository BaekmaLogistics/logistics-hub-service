package com.sparta.logistics.infrastructure.cache;

import com.sparta.logistics.domain.model.ShortestPath;

import java.util.List;
import java.util.UUID;

public record ShortestPathCacheValue(
        List<UUID> path,
        double totalDistance,
        int totalDuration
) {
    public static ShortestPathCacheValue from(ShortestPath shortestPath){
        return new ShortestPathCacheValue(
                shortestPath.path(),
                shortestPath.totalDistance(),
                shortestPath.totalDuration()
        );
    }

    public ShortestPath toDistance(){
        return new ShortestPath(
                path,
                totalDistance,
                totalDuration
        );
    }
}
