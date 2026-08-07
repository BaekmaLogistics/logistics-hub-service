package com.sparta.logistics.application.command.service;

import com.sparta.logistics.application.command.dto.hubroute.DeleteHubRouteCommand;
import com.sparta.logistics.application.event.HubRouteChangedEvent;
import com.sparta.logistics.common.code.ErrorResponseCode;
import com.sparta.logistics.common.exception.ApiException;
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
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
class DeleteHubRouteServiceTest {

    @Mock
    private HubRouteRepository hubRouteRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private DeleteHubRouteService deleteHubRouteService;

    @Test
    @DisplayName("허브 경로 삭제 성공")
    void deleteHubRoute_success() {
        // given
        UUID routeId = UUID.randomUUID();
        UUID deletedBy = UUID.randomUUID();

        Hub fromHub = Hub.builder()
                .name("서울 허브")
                .address("서울")
                .latitude(37.1)
                .longitude(127.1)
                .build();

        Hub toHub = Hub.builder()
                .name("대전 허브")
                .address("대전")
                .latitude(36.3)
                .longitude(127.3)
                .build();

        HubRoute hubRoute = HubRoute.builder()
                .fromHub(fromHub)
                .toHub(toHub)
                .distance(150.0)
                .duration(120)
                .build();

        ReflectionTestUtils.setField(hubRoute, "id", routeId);

        given(hubRouteRepository.findById(routeId))
                .willReturn(Optional.of(hubRoute));

        DeleteHubRouteCommand command = DeleteHubRouteCommand.builder()
                .id(routeId)
                .deletedBy(deletedBy)
                .build();

        // when
        deleteHubRouteService.deleteHubRoute(command);

        // then
        assertThat(hubRoute.isDeleted()).isTrue();
        assertThat(hubRoute.getDeletedBy()).isEqualTo(deletedBy);

        verify(eventPublisher)
                .publishEvent(any(HubRouteChangedEvent.class));
    }

    @Test
    @DisplayName("존재하지 않는 허브 경로 삭제 시 예외가 발생한다")
    void deleteHubRoute_fail_notFound() {
        // given
        UUID routeId = UUID.randomUUID();

        given(hubRouteRepository.findById(routeId))
                .willReturn(Optional.empty());

        DeleteHubRouteCommand command = DeleteHubRouteCommand.builder()
                .id(routeId)
                .deletedBy(UUID.randomUUID())
                .build();

        // when & then
        assertThatThrownBy(() ->
                deleteHubRouteService.deleteHubRoute(command)
        )
                .isInstanceOf(ApiException.class)
                .hasMessage(
                        ErrorResponseCode.HUB_ROUTE_NOT_FOUND.getMessage()
                );

        verifyNoInteractions(eventPublisher);
    }

    @Test
    @DisplayName("이미 삭제된 허브 경로를 다시 삭제하면 예외가 발생한다")
    void deleteHubRoute_fail_alreadyDeleted() {
        // given
        UUID routeId = UUID.randomUUID();

        HubRoute hubRoute = HubRoute.builder()
                .fromHub(Hub.builder()
                        .name("서울 허브")
                        .address("서울")
                        .latitude(37.1)
                        .longitude(127.1)
                        .build())
                .toHub(Hub.builder()
                        .name("대전 허브")
                        .address("대전")
                        .latitude(36.3)
                        .longitude(127.3)
                        .build())
                .distance(150.0)
                .duration(120)
                .build();

        hubRoute.softDelete(UUID.randomUUID());

        given(hubRouteRepository.findById(routeId))
                .willReturn(Optional.of(hubRoute));

        DeleteHubRouteCommand command = DeleteHubRouteCommand.builder()
                .id(routeId)
                .deletedBy(UUID.randomUUID())
                .build();

        // when & then
        assertThatThrownBy(() ->
                deleteHubRouteService.deleteHubRoute(command)
        )
                .isInstanceOf(ApiException.class)
                .hasMessage(
                        ErrorResponseCode.HUB_ROUTE_ALREADY_DELETED.getMessage()
                );

        verifyNoInteractions(eventPublisher);
    }
}