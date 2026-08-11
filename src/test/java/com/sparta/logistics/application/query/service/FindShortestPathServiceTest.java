package com.sparta.logistics.application.query.service;

import com.sparta.logistics.application.graph.HubGraphManager;
import com.sparta.logistics.application.port.ShortestPathCache;
import com.sparta.logistics.application.query.dto.ShortestPathResponse;
import com.sparta.logistics.domain.graph.HubGraph;
import com.sparta.logistics.domain.graph.PathFinder;
import com.sparta.logistics.domain.model.PathSegment;
import com.sparta.logistics.domain.model.ShortestPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FindShortestPathServiceTest {

    @Mock
    private HubGraphManager hubGraphManager;

    @Mock
    private PathFinder pathFinder;

    @Mock
    private ShortestPathCache shortestPathCache;

    @InjectMocks
    private FindShortestPathService findShortestPathService;

    private UUID fromHubId;
    private UUID toHubId;
    private ShortestPath shortestPath;
    private long graphVersion;

    @BeforeEach
    void setUp() {
        graphVersion = 3L;
        fromHubId = UUID.randomUUID();
        toHubId = UUID.randomUUID();

        PathSegment segment = new PathSegment(
                fromHubId,
                toHubId,
                100.0,
                60
        );

        shortestPath = new ShortestPath(
                List.of(fromHubId, toHubId),
                List.of(segment),
                100.0,
                60
        );
    }

    @Test
    @DisplayName("캐시에 최단 경로가 존재하면 Dijkstra를 실행하지 않고 캐시 결과를 반환한다")
    void findShortestPath_cacheHit() {

        when(hubGraphManager.getLocalGraphVersion())
                .thenReturn(graphVersion);

        // given
        when(shortestPathCache.get(graphVersion, fromHubId, toHubId))
                .thenReturn(Optional.of(shortestPath));

        // when
        ShortestPathResponse response =
                findShortestPathService.findShortestPath(
                        fromHubId,
                        toHubId
                );

        // then
        assertEquals(shortestPath.path(), response.getHubIds());
        assertEquals(shortestPath.segments(), response.getSegments());
        assertEquals(shortestPath.totalDistance(), response.getTotalDistance());
        assertEquals(shortestPath.totalDuration(), response.getTotalDuration());

        verify(hubGraphManager).getLocalGraphVersion();

        verify(shortestPathCache)
                .get(graphVersion, fromHubId, toHubId);

        verify(pathFinder, never())
                .findShortestPath(any(), any(), any());

        verify(shortestPathCache, never())
                .put(anyLong(), any(), any(), any());
    }

    @Test
    @DisplayName("캐시에 최단 경로가 없으면 Dijkstra로 계산하고 결과를 캐시에 저장한다")
    void findShortestPath_cacheMiss() {

        // given
        HubGraph hubGraph = mock(HubGraph.class);

        when(shortestPathCache.get(graphVersion, fromHubId, toHubId))
                .thenReturn(Optional.empty());

        when(hubGraphManager.getGraph())
                .thenReturn(hubGraph);

        when(pathFinder.findShortestPath(
                hubGraph,
                fromHubId,
                toHubId
        )).thenReturn(shortestPath);

        // when
        ShortestPathResponse response =
                findShortestPathService.findShortestPath(
                        fromHubId,
                        toHubId
                );

        // then
        assertEquals(shortestPath.path(), response.getHubIds());
        assertEquals(shortestPath.segments(), response.getSegments());
        assertEquals(shortestPath.totalDistance(), response.getTotalDistance());
        assertEquals(shortestPath.totalDuration(), response.getTotalDuration());

        verify(hubGraphManager).getLocalGraphVersion();

        verify(shortestPathCache)
                .get(graphVersion, fromHubId, toHubId);

        verify(hubGraphManager)
                .getGraph();

        verify(pathFinder)
                .findShortestPath(
                        hubGraph,
                        fromHubId,
                        toHubId
                );

        verify(shortestPathCache)
                .put(
                        graphVersion,
                        fromHubId,
                        toHubId,
                        shortestPath
                );
    }
}