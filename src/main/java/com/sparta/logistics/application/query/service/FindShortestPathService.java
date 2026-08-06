package com.sparta.logistics.application.query.service;

import com.sparta.logistics.application.graph.HubGraphManager;
import com.sparta.logistics.application.query.dto.ShortestPathResponse;
import com.sparta.logistics.application.query.usecase.FindShortestPathUseCase;
import com.sparta.logistics.domain.graph.HubGraph;
import com.sparta.logistics.domain.graph.PathFinder;
import com.sparta.logistics.domain.model.ShortestPath;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FindShortestPathService implements FindShortestPathUseCase {

    private final HubGraphManager hubGraphManager;
    private final PathFinder pathFinder;

    @Override
    public ShortestPathResponse findShortestPath(
            UUID fromHubId,
            UUID toHubId
    ){
        HubGraph hubGraph = hubGraphManager.getGraph();

        ShortestPath shortestPath = pathFinder.findShortestPath(
                hubGraph,
                fromHubId,
                toHubId
        );

        //TODO: Redis 적재

        return ShortestPathResponse.from(shortestPath);
    }
}
