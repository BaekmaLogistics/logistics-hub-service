package com.sparta.logistics.application.command.service;

import com.sparta.logistics.application.command.dto.hubroute.CreateHubRouteCommand;
import com.sparta.logistics.application.command.dto.hubroute.CreateHubRouteResponse;
import com.sparta.logistics.application.common.service.DirectionService;
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
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
class CreateHubRouteServiceTest {

    @InjectMocks
    private CreateHubRouteService createHubRouteService;

    @Mock
    private HubRepository hubRepository;

    @Mock
    private HubRouteRepository hubRouteRepository;

    @Mock
    private DirectionService directionService;

    @Test
    @DisplayName("허브 연결을 생성한다")
    void createHubRoute_success() {
        UUID fromHubId = UUID.randomUUID();
        UUID toHubId = UUID.randomUUID();

        CreateHubRouteCommand command = CreateHubRouteCommand.builder()
                .fromHubId(fromHubId)
                .toHubId(toHubId)
                .build();

        Hub fromHub = Hub.builder()
                .name("서울")
                .latitude(37.514575)
                .longitude(127.105399)
                .build();

        ReflectionTestUtils.setField(fromHub, "id", fromHubId);

        Hub toHub = Hub.builder()
                .name("경기북부")
                .latitude(37.658359)
                .longitude(126.832020)
                .build();

        ReflectionTestUtils.setField(toHub, "id", toHubId);

        RouteInfo routeInfo = RouteInfo.builder()
                .distance(35.8)
                .duration(42)
                .build();

        when(hubRepository.findById(fromHubId))
                .thenReturn(Optional.of(fromHub));

        when(hubRepository.findById(toHubId))
                .thenReturn(Optional.of(toHub));

        when(hubRouteRepository.existsByFromHubIdAndToHubId(fromHubId, toHubId))
                .thenReturn(false);

        when(directionService.getRoute(fromHub, toHub))
                .thenReturn(routeInfo);

        HubRoute savedHubRoute = HubRoute.builder()
                .fromHubId(fromHubId)
                .toHubId(toHubId)
                .distance(35.8)
                .duration(42)
                .build();

        ReflectionTestUtils.setField(savedHubRoute, "id", UUID.randomUUID());

        when(hubRouteRepository.saveAndFlush(any(HubRoute.class)))
                .thenReturn(savedHubRoute);

        // when
        CreateHubRouteResponse response =
                createHubRouteService.create(command);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getFromHubId()).isEqualTo(fromHubId);
        assertThat(response.getToHubId()).isEqualTo(toHubId);
        assertThat(response.getDistance()).isEqualTo(35.8);
        assertThat(response.getDuration()).isEqualTo(42);

        verify(hubRepository).findById(fromHubId);
        verify(hubRepository).findById(toHubId);
        verify(directionService).getRoute(fromHub, toHub);
        verify(hubRouteRepository).saveAndFlush(any(HubRoute.class));
    }

    @Test
    @DisplayName("출발 허브와 도착 허브가 같으면 예외가 발생한다.")
    void createHubRoute_sameHub() {
        // given
        UUID hubId = UUID.randomUUID();

        CreateHubRouteCommand command = CreateHubRouteCommand.builder()
                .fromHubId(hubId)
                .toHubId(hubId)
                .build();

        // when & then
        ApiException exception = assertThrows(
                ApiException.class,
                () -> createHubRouteService.create(command)
        );

        assertThat(exception.getResponseCode())
                .isEqualTo(ErrorResponseCode.INVALID_HUB_ROUTE);

        verifyNoInteractions(hubRepository);
        verifyNoInteractions(directionService);
        verifyNoInteractions(hubRouteRepository);
    }

    @Test
    @DisplayName("출발 허브가 존재하지 않으면 예외가 발생한다.")
    void createHubRoute_fromHubNotFound() {
        // given
        UUID fromHubId = UUID.randomUUID();
        UUID toHubId = UUID.randomUUID();

        CreateHubRouteCommand command = CreateHubRouteCommand.builder()
                .fromHubId(fromHubId)
                .toHubId(toHubId)
                .build();

        when(hubRepository.findById(fromHubId))
                .thenReturn(Optional.empty());

        // when & then
        ApiException exception = assertThrows(
                ApiException.class,
                () -> createHubRouteService.create(command)
        );

        assertThat(exception.getResponseCode())
                .isEqualTo(ErrorResponseCode.HUB_NOT_FOUND);

        verify(hubRepository).findById(fromHubId);
        verify(directionService, never()).getRoute(any(), any());
        verify(hubRouteRepository, never()).save(any());
    }

    @Test
    @DisplayName("도착 허브가 존재하지 않으면 예외가 발생한다.")
    void createHubRoute_toHubNotFound() {
        // given
        UUID fromHubId = UUID.randomUUID();
        UUID toHubId = UUID.randomUUID();

        CreateHubRouteCommand command = CreateHubRouteCommand.builder()
                .fromHubId(fromHubId)
                .toHubId(toHubId)
                .build();

        Hub fromHub = Hub.builder()
                .name("테스트 허브")
                .address("서울특별시")
                .latitude(37.5665)
                .longitude(126.9780)
                .build();

        ReflectionTestUtils.setField(fromHub, "id", fromHubId);

        when(hubRepository.findById(fromHubId))
                .thenReturn(Optional.of(fromHub));

        when(hubRepository.findById(toHubId))
                .thenReturn(Optional.empty());

        // when & then
        ApiException exception = assertThrows(
                ApiException.class,
                () -> createHubRouteService.create(command)
        );

        assertThat(exception.getResponseCode())
                .isEqualTo(ErrorResponseCode.HUB_NOT_FOUND);

        verify(directionService, never()).getRoute(any(), any());
        verify(hubRouteRepository, never()).save(any());
    }

    @Test
    @DisplayName("이미 존재하는 허브 연결이면 예외가 발생한다.")
    void createHubRoute_alreadyExists() {
        // given
        UUID fromHubId = UUID.randomUUID();
        UUID toHubId = UUID.randomUUID();

        CreateHubRouteCommand command = CreateHubRouteCommand.builder()
                .fromHubId(fromHubId)
                .toHubId(toHubId)
                .build();

        when(hubRouteRepository.existsByFromHubIdAndToHubId(fromHubId, toHubId))
                .thenReturn(true);

        ApiException exception = assertThrows(
                ApiException.class,
                () -> createHubRouteService.create(command)
        );

        assertThat(exception.getResponseCode())
                .isEqualTo(ErrorResponseCode.HUB_ROUTE_ALREADY_EXISTS);

        verify(hubRouteRepository).existsByFromHubIdAndToHubId(fromHubId, toHubId);
        verify(directionService, never()).getRoute(any(), any());
        verify(hubRouteRepository, never()).save(any());
    }
}