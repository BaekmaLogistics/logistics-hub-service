package com.sparta.logistics.application.command.service;

import com.sparta.logistics.application.common.service.DirectionService;
import com.sparta.logistics.application.event.HubRouteChangedEvent;
import com.sparta.logistics.domain.entity.Hub;
import com.sparta.logistics.domain.entity.HubRoute;
import com.sparta.logistics.domain.repository.HubRouteRepository;
import com.sparta.logistics.infrastructure.feign.dto.direction.RouteInfo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
class RefreshHubRouteServiceTest {

    @Mock
    private HubRouteRepository hubRouteRepository;

    @Mock
    private DirectionService directionService;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private RefreshHubRouteService refreshHubRouteService;

    @Test
    @DisplayName("허브와 연결된 활성 경로들의 거리와 시간을 재계산한다")
    void refreshRoutesByHub_success() {
        // given
        Hub seoulHub = Hub.builder()
                .name("서울 허브")
                .address("서울특별시")
                .latitude(37.5665)
                .longitude(126.9780)
                .build();

        Hub daejeonHub = Hub.builder()
                .name("대전 허브")
                .address("대전광역시")
                .latitude(36.3504)
                .longitude(127.3845)
                .build();

        Hub incheonHub = Hub.builder()
                .name("인천 허브")
                .address("인천광역시")
                .latitude(37.4563)
                .longitude(126.7052)
                .build();

        HubRoute seoulToDaejeon = HubRoute.builder()
                .fromHub(seoulHub)
                .toHub(daejeonHub)
                .distance(150.0)
                .duration(120)
                .build();

        HubRoute incheonToSeoul = HubRoute.builder()
                .fromHub(incheonHub)
                .toHub(seoulHub)
                .distance(50.0)
                .duration(60)
                .build();

        given(hubRouteRepository.findAllActiveRoutesByHub(seoulHub))
                .willReturn(List.of(
                        seoulToDaejeon,
                        incheonToSeoul
                ));

        RouteInfo seoulDaejeonRouteInfo = mock(RouteInfo.class);
        RouteInfo incheonSeoulRouteInfo = mock(RouteInfo.class);

        given(directionService.getRoute(seoulHub, daejeonHub))
                .willReturn(seoulDaejeonRouteInfo);

        given(directionService.getRoute(incheonHub, seoulHub))
                .willReturn(incheonSeoulRouteInfo);

        given(seoulDaejeonRouteInfo.getDistance())
                .willReturn(160.0);
        given(seoulDaejeonRouteInfo.getDuration())
                .willReturn(130);

        given(incheonSeoulRouteInfo.getDistance())
                .willReturn(55.0);
        given(incheonSeoulRouteInfo.getDuration())
                .willReturn(65);

        // when
        refreshHubRouteService.refreshRoutesByHub(seoulHub);

        // then
        assertThat(seoulToDaejeon.getDistance()).isEqualTo(160.0);
        assertThat(seoulToDaejeon.getDuration()).isEqualTo(130);

        assertThat(incheonToSeoul.getDistance()).isEqualTo(55.0);
        assertThat(incheonToSeoul.getDuration()).isEqualTo(65);

        verify(directionService)
                .getRoute(seoulHub, daejeonHub);

        verify(directionService)
                .getRoute(incheonHub, seoulHub);

        verify(eventPublisher)
                .publishEvent(any(HubRouteChangedEvent.class));
    }

}