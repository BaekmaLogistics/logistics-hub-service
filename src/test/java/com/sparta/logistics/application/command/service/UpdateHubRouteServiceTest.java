package com.sparta.logistics.application.command.service;

import com.sparta.logistics.application.command.dto.hubroute.UpdateHubRouteCommand;
import com.sparta.logistics.application.command.dto.hubroute.UpdateHubRouteResponse;
import com.sparta.logistics.application.common.service.DirectionService;
import com.sparta.logistics.application.event.HubRouteChangedEvent;
import com.sparta.logistics.common.code.ErrorResponseCode;
import com.sparta.logistics.common.exception.ApiException;
import com.sparta.logistics.domain.entity.Hub;
import com.sparta.logistics.domain.entity.HubRoute;
import com.sparta.logistics.domain.repository.HubRepository;
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
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
class UpdateHubRouteServiceTest {

    @Mock
    private HubRouteRepository hubRouteRepository;

    @Mock
    private HubRepository hubRepository;

    @Mock
    private DirectionService directionService;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private UpdateHubRouteService updateHubRouteService;

    @Test
    @DisplayName("도착 허브를 수정하면 거리와 시간을 재계산하고 변경 이벤트를 발행한다")
    void updateHubRoute_success() {
        // given
        UUID routeId = UUID.randomUUID();
        UUID fromHubId = UUID.randomUUID();
        UUID oldToHubId = UUID.randomUUID();
        UUID newToHubId = UUID.randomUUID();

        Hub fromHub = Hub.builder()
                .name("서울 허브")
                .address("서울특별시")
                .latitude(37.5665)
                .longitude(126.9780)
                .build();

        ReflectionTestUtils.setField(fromHub, "id", fromHubId);

        Hub oldToHub = Hub.builder()
                .name("대전 허브")
                .address("대전광역시")
                .latitude(36.3504)
                .longitude(127.3845)
                .build();

        ReflectionTestUtils.setField(oldToHub, "id", oldToHubId);

        Hub newToHub = Hub.builder()
                .name("대구 허브")
                .address("대구광역시")
                .latitude(35.8714)
                .longitude(128.6014)
                .build();

        ReflectionTestUtils.setField(newToHub, "id", newToHubId);

        HubRoute hubRoute = HubRoute.builder()
                .fromHub(fromHub)
                .toHub(oldToHub)
                .distance(150.0)
                .duration(120)
                .build();

        ReflectionTestUtils.setField(hubRoute, "id", routeId);

        RouteInfo routeInfo = mock(RouteInfo.class);

        given(hubRouteRepository.findById(routeId))
                .willReturn(Optional.of(hubRoute));

        given(hubRepository.findById(newToHubId))
                .willReturn(Optional.of(newToHub));

        given(
                hubRouteRepository
                        .existsByFromHubAndToHubAndIdNotAndDeletedAtIsNull(
                                fromHub,
                                newToHub,
                                routeId
                        )
        ).willReturn(false);

        given(directionService.getRoute(fromHub, newToHub))
                .willReturn(routeInfo);

        given(routeInfo.getDistance()).willReturn(300.0);
        given(routeInfo.getDuration()).willReturn(180);

        UpdateHubRouteCommand command = UpdateHubRouteCommand.builder()
                .id(routeId)
                .toHubId(newToHubId)
                .build();

        // when
        UpdateHubRouteResponse response =
                updateHubRouteService.updateHubRoute(command);

        // then
        assertThat(hubRoute.getFromHub()).isEqualTo(fromHub);
        assertThat(hubRoute.getToHub()).isEqualTo(newToHub);
        assertThat(hubRoute.getDistance()).isEqualTo(300.0);
        assertThat(hubRoute.getDuration()).isEqualTo(180);

        assertThat(response.getFromHubId()).isEqualTo(fromHubId);
        assertThat(response.getToHubId()).isEqualTo(newToHubId);
        assertThat(response.getDistance()).isEqualTo(300.0);
        assertThat(response.getDuration()).isEqualTo(180);

        verify(directionService).getRoute(fromHub, newToHub);
        verify(eventPublisher)
                .publishEvent(any(HubRouteChangedEvent.class));
    }

    @Test
    @DisplayName("이미 존재하는 활성 허브 경로로 수정하면 예외가 발생한다")
    void updateHubRoute_fail_duplicateRoute() {
        // given
        UUID routeId = UUID.randomUUID();
        UUID fromHubId = UUID.randomUUID();
        UUID oldToHubId = UUID.randomUUID();
        UUID newToHubId = UUID.randomUUID();

        Hub fromHub = Hub.builder()
                .name("서울 허브")
                .address("서울특별시")
                .latitude(37.5665)
                .longitude(126.9780)
                .build();
        ReflectionTestUtils.setField(fromHub, "id", fromHubId);

        Hub oldToHub = Hub.builder()
                .name("대전 허브")
                .address("대전광역시")
                .latitude(36.3504)
                .longitude(127.3845)
                .build();
        ReflectionTestUtils.setField(oldToHub, "id", oldToHubId);

        Hub newToHub = Hub.builder()
                .name("대구 허브")
                .address("대구광역시")
                .latitude(35.8714)
                .longitude(128.6014)
                .build();
        ReflectionTestUtils.setField(newToHub, "id", newToHubId);

        HubRoute hubRoute = HubRoute.builder()
                .fromHub(fromHub)
                .toHub(oldToHub)
                .distance(150.0)
                .duration(120)
                .build();
        ReflectionTestUtils.setField(hubRoute, "id", routeId);

        given(hubRouteRepository.findById(routeId))
                .willReturn(Optional.of(hubRoute));

        given(hubRepository.findById(newToHubId))
                .willReturn(Optional.of(newToHub));

        given(
                hubRouteRepository
                        .existsByFromHubAndToHubAndIdNotAndDeletedAtIsNull(
                                fromHub,
                                newToHub,
                                routeId
                        )
        ).willReturn(true);

        UpdateHubRouteCommand command = UpdateHubRouteCommand.builder()
                .id(routeId)
                .toHubId(newToHubId)
                .build();

        // when & then
        assertThatThrownBy(
                () -> updateHubRouteService.updateHubRoute(command)
        )
                .isInstanceOf(ApiException.class)
                .hasMessage(
                        ErrorResponseCode.HUB_ROUTE_ALREADY_EXISTS.getMessage()
                );

        verifyNoInteractions(directionService);
        verifyNoInteractions(eventPublisher);
    }

    @Test
    @DisplayName("출발 허브와 도착 허브가 같으면 예외가 발생한다")
    void updateHubRoute_fail_sameHub() {
        // given
        UUID routeId = UUID.randomUUID();
        UUID hubId = UUID.randomUUID();
        UUID oldToHubId = UUID.randomUUID();

        Hub fromHub = Hub.builder()
                .name("서울 허브")
                .address("서울특별시")
                .latitude(37.5665)
                .longitude(126.9780)
                .build();
        ReflectionTestUtils.setField(fromHub, "id", hubId);

        Hub oldToHub = Hub.builder()
                .name("대전 허브")
                .address("대전광역시")
                .latitude(36.3504)
                .longitude(127.3845)
                .build();
        ReflectionTestUtils.setField(oldToHub, "id", oldToHubId);

        HubRoute hubRoute = HubRoute.builder()
                .fromHub(fromHub)
                .toHub(oldToHub)
                .distance(150.0)
                .duration(120)
                .build();
        ReflectionTestUtils.setField(hubRoute, "id", routeId);

        given(hubRouteRepository.findById(routeId))
                .willReturn(Optional.of(hubRoute));

        // toHub를 기존 fromHub와 같은 허브로 변경
        given(hubRepository.findById(hubId))
                .willReturn(Optional.of(fromHub));

        UpdateHubRouteCommand command = UpdateHubRouteCommand.builder()
                .id(routeId)
                .toHubId(hubId)
                .build();

        // when & then
        assertThatThrownBy(
                () -> updateHubRouteService.updateHubRoute(command)
        )
                .isInstanceOf(ApiException.class)
                .hasMessage(ErrorResponseCode.INVALID_HUB_ROUTE.getMessage());

        verifyNoInteractions(directionService);
        verifyNoInteractions(eventPublisher);
    }
}