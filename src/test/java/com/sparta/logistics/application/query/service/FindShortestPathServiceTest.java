package com.sparta.logistics.application.query.service;

import com.sparta.logistics.application.graph.HubGraphManager;
import com.sparta.logistics.application.query.dto.ShortestPathResponse;
import com.sparta.logistics.domain.graph.HubGraph;
import com.sparta.logistics.domain.graph.PathFinder;
import com.sparta.logistics.domain.model.ShortestPath;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
class FindShortestPathServiceTest {

    @Mock
    private HubGraphManager hubGraphManager;

    @Mock
    private PathFinder pathFinder;

    @InjectMocks
    private FindShortestPathService findShortestPathService;

    @Test
    @DisplayName("최적 경로 조회하기")
    void findShortestPath() {
        UUID fromHubId = UUID.randomUUID();
        UUID middleHubId = UUID.randomUUID();
        UUID toHubId = UUID.randomUUID();

        HubGraph hubGraph = mock(HubGraph.class);

        when(hubGraphManager.getGraph())
                .thenReturn(hubGraph);

        ShortestPath shortestPath = new ShortestPath(
                List.of(fromHubId, middleHubId, toHubId),
                120.5,
                95
        );

        when(hubGraphManager.getGraph()).thenReturn(hubGraph);
        when(pathFinder.findShortestPath(hubGraph, fromHubId, toHubId)).thenReturn(shortestPath);

        ShortestPathResponse response = findShortestPathService.findShortestPath(fromHubId, toHubId);

        assertThat(response.getHubIds()).containsExactly(fromHubId, middleHubId, toHubId);
        assertThat(response.getTotalDistance()).isEqualTo(120.5);
        assertThat(response.getTotalDuration()).isEqualTo(95);

        verify(hubGraphManager).getGraph();
        verify(pathFinder).findShortestPath(hubGraph, fromHubId, toHubId);
    }

}