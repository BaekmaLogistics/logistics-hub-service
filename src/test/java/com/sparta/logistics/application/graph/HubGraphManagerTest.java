package com.sparta.logistics.application.graph;

import com.sparta.logistics.domain.entity.Hub;
import com.sparta.logistics.domain.entity.HubRoute;
import com.sparta.logistics.domain.graph.Edge;
import com.sparta.logistics.domain.graph.HubGraph;
import com.sparta.logistics.domain.graph.HubNode;
import com.sparta.logistics.domain.repository.HubRepository;
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
import static org.mockito.Mockito.*;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
class HubGraphManagerTest {

    @Mock
    private HubRouteRepository hubRouteRepository;

    @Mock
    private HubRepository hubRepository;

    @InjectMocks
    private HubGraphManager hubGraphManager;

    @Test
    @DisplayName("HubRoute 목록으로 메모리 Graph를 생성한다.")
    void make_graph() {

        // given
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

        when(seoul.getLatitude()).thenReturn(37.0);
        when(seoul.getLongitude()).thenReturn(127.0);

        when(gyeonggi.getLatitude()).thenReturn(37.1);
        when(gyeonggi.getLongitude()).thenReturn(127.1);

        when(incheon.getLatitude()).thenReturn(37.2);
        when(incheon.getLongitude()).thenReturn(126.6);

        when(chungbuk.getLatitude()).thenReturn(36.6);
        when(chungbuk.getLongitude()).thenReturn(127.5);

        when(hubRepository.findAllByDeletedAtIsNull())
                .thenReturn(List.of(seoul, gyeonggi, incheon, chungbuk));

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

        // when
        hubGraphManager.reloadGraph();

        HubGraph graph = hubGraphManager.getGraph();

        // then
        HubNode seoulNode = graph.getNode(seoulId);

        assertThat(seoulNode).isNotNull();
        assertThat(seoulNode.getEdges()).hasSize(2);

        assertThat(seoulNode.getEdges())
                .extracting(Edge::getToHubId)
                .containsExactlyInAnyOrder(
                        gyeonggiId,
                        incheonId
                );

        HubNode gyeonggiNode = graph.getNode(gyeonggiId);

        assertThat(gyeonggiNode).isNotNull();
        assertThat(gyeonggiNode.getEdges()).hasSize(1);

        assertThat(gyeonggiNode.getEdges().get(0).getToHubId())
                .isEqualTo(chungbukId);
    }
}