package com.sparta.logistics.application.graph;

import com.sparta.logistics.domain.entity.Hub;
import com.sparta.logistics.domain.entity.HubRoute;
import com.sparta.logistics.domain.repository.HubRouteRepository;
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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
class HubGraphManagerTest {

    @Mock
    private HubRouteRepository hubRouteRepository;

    @InjectMocks
    private HubGraphManager hubGraphManager;

    @Test
    @DisplayName("HubRoute 목록으로 메모리 Graph 만들기")
    void make_graph(){
        UUID seoulId = UUID.randomUUID();
        UUID gyeonggiId = UUID.randomUUID();
        UUID incheonId = UUID.randomUUID();
        UUID chungbukId = UUID.randomUUID();

        Hub seoul = mock(Hub.class);
        Hub gyeonggi = mock(Hub.class);
        Hub incheon = mock(Hub.class);
        Hub chungbuk = mock(Hub.class);

        when(seoul.getId()).thenReturn(seoulId);
        when(gyeonggi.getId()).thenReturn(gyeonggiId);
        when(incheon.getId()).thenReturn(incheonId);
        when(chungbuk.getId()).thenReturn(chungbukId);

        HubRoute route1 = mock(HubRoute.class);

        when(route1.getFromHub()).thenReturn(seoul);
        when(route1.getToHub()).thenReturn(gyeonggi);

        when(route1.getDistance()).thenReturn(30.0);
        when(route1.getDuration()).thenReturn(40);

        HubRoute route2 = mock(HubRoute.class);

        when(route2.getFromHub()).thenReturn(seoul);
        when(route2.getToHub()).thenReturn(incheon);

        when(route2.getDistance()).thenReturn(60.0);
        when(route2.getDuration()).thenReturn(50);

        HubRoute route3 = mock(HubRoute.class);

        when(route3.getFromHub()).thenReturn(gyeonggi);
        when(route3.getToHub()).thenReturn(chungbuk);

        when(route3.getDistance()).thenReturn(80.0);
        when(route3.getDuration()).thenReturn(60);

        when(hubRouteRepository.findAllByDeletedAtIsNull())
                .thenReturn(List.of(route1, route2, route3));

        hubGraphManager.reloadGraph();

        HubGraph graph = hubGraphManager.getGraph();

        List<Edge> seoulEdges = graph.getEdges(seoulId);

        assertThat(seoulEdges).hasSize(2);

        assertThat(seoulEdges)
                .extracting(Edge::getToHubId)
                .containsExactlyInAnyOrder(
                        gyeonggiId,
                        incheonId
                );

        List<Edge> gyeonggiEdges = graph.getEdges(gyeonggiId);

        assertThat(gyeonggiEdges).hasSize(1);

        assertThat(gyeonggiEdges.get(0).getToHubId())
                .isEqualTo(chungbukId);
    }

}