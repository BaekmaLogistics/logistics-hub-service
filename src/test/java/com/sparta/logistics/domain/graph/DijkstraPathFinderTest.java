package com.sparta.logistics.domain.graph;
// A ----10---- B
// |            |
// 30           5
// |            |
// C ----8----- D

import com.sparta.logistics.common.exception.ApiException;
import com.sparta.logistics.domain.model.ShortestPath;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

@Tag("unit")
class DijkstraPathFinderTest {

    private final DijkstraPathFinder pathFinder = new DijkstraPathFinder();

    @Test
    @DisplayName("최단 경로를 계산한다.")
    void findShortestPath_success() {

        // given
        UUID a = UUID.randomUUID();
        UUID b = UUID.randomUUID();
        UUID c = UUID.randomUUID();
        UUID d = UUID.randomUUID();

        HubNode nodeA = new HubNode(a, 0, 0);
        HubNode nodeB = new HubNode(b, 0, 0);
        HubNode nodeC = new HubNode(c, 0, 0);
        HubNode nodeD = new HubNode(d, 0, 0);

        nodeA.addEdge(new Edge(b, 10, 10));
        nodeA.addEdge(new Edge(c, 30, 30));

        nodeB.addEdge(new Edge(d, 5, 5));

        nodeC.addEdge(new Edge(d, 8, 8));

        Map<UUID, HubNode> nodes = new HashMap<>();

        nodes.put(a, nodeA);
        nodes.put(b, nodeB);
        nodes.put(c, nodeC);
        nodes.put(d, nodeD);

        HubGraph graph = new HubGraph(nodes);

        // when
        ShortestPath result =
                pathFinder.findShortestPath(graph, a, d);

        // then
        assertThat(result.totalDistance()).isEqualTo(15);

        assertThat(result.totalDuration()).isEqualTo(15);

        assertThat(result.path())
                .containsExactly(a, b, d);
    }

    @Test
    @DisplayName("더 짧은 경로가 존재하면 기존 경로를 갱신한다.")
    void updateShortestPath() {

        // given
        UUID a = UUID.randomUUID();
        UUID b = UUID.randomUUID();
        UUID c = UUID.randomUUID();

        HubNode nodeA = new HubNode(a, 0, 0);
        HubNode nodeB = new HubNode(b, 0, 0);
        HubNode nodeC = new HubNode(c, 0, 0);

        nodeA.addEdge(new Edge(b, 20, 20));
        nodeA.addEdge(new Edge(c, 5, 5));

        nodeC.addEdge(new Edge(b, 5, 5));

        Map<UUID, HubNode> nodes = new HashMap<>();

        nodes.put(a, nodeA);
        nodes.put(b, nodeB);
        nodes.put(c, nodeC);

        HubGraph graph = new HubGraph(nodes);

        // when
        ShortestPath result =
                pathFinder.findShortestPath(graph, a, b);

        // then
        assertThat(result.totalDistance()).isEqualTo(10);

        assertThat(result.totalDuration()).isEqualTo(10);

        assertThat(result.path())
                .containsExactly(a, c, b);
    }

    @Test
    @DisplayName("경로가 존재하지 않으면 예외를 발생시킨다.")
    void throwException_whenPathNotFound() {

        // given
        UUID a = UUID.randomUUID();
        UUID b = UUID.randomUUID();
        UUID c = UUID.randomUUID();

        HubNode nodeA = new HubNode(a, 0, 0);
        HubNode nodeB = new HubNode(b, 0, 0);
        HubNode nodeC = new HubNode(c, 0, 0);

        nodeA.addEdge(new Edge(b, 10, 10));

        Map<UUID, HubNode> nodes = new HashMap<>();

        nodes.put(a, nodeA);
        nodes.put(b, nodeB);
        nodes.put(c, nodeC);

        HubGraph graph = new HubGraph(nodes);

        // when & then
        assertThatThrownBy(() ->
                pathFinder.findShortestPath(graph, a, c))
                .isInstanceOf(ApiException.class);
    }
}