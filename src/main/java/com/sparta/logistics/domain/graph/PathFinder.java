package com.sparta.logistics.domain.graph;

import com.sparta.logistics.domain.model.ShortestPath;

import java.util.UUID;

public interface PathFinder {

    ShortestPath findShortestPath(
            HubGraph graph,
            UUID fromHubId,
            UUID toHubId
    );
}
