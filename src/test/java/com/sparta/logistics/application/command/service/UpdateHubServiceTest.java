package com.sparta.logistics.application.command.service;

import com.sparta.logistics.application.command.dto.hub.UpdateHubCommand;
import com.sparta.logistics.application.command.dto.hub.UpdateHubResponse;
import com.sparta.logistics.application.command.usecase.RefreshHubRouteUseCase;
import com.sparta.logistics.application.common.dto.Coordinate;
import com.sparta.logistics.application.common.service.GeocodingService;
import com.sparta.logistics.common.code.ErrorResponseCode;
import com.sparta.logistics.common.exception.ApiException;
import com.sparta.logistics.domain.entity.Hub;
import com.sparta.logistics.domain.repository.HubRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
class UpdateHubServiceTest {

    @Mock
    private HubRepository hubRepository;

    @Mock
    private GeocodingService geocodingService;

    @Mock
    private RefreshHubRouteUseCase refreshHubRouteUseCase;

    @InjectMocks
    private UpdateHubService updateHubService;

    @Test
    @DisplayName("허브 수정 성공")
    void updateHub_success() {
        UUID hubId = UUID.randomUUID();

        Hub hub = Hub.builder()
                .name("서울 허브")
                .address("서울")
                .latitude(37.1)
                .longitude(127.1)
                .build();

        given(hubRepository.findById(hubId))
                .willReturn(Optional.of(hub));

        UpdateHubCommand command = UpdateHubCommand.builder()
                .id(hubId)
                .name("서울 허브")
                .address("경기도 성남시")
                .build();

        Coordinate coordinate =
                new Coordinate(37.222,127.333);

        given(hubRepository.findById(hubId))
                .willReturn(Optional.of(hub));

        given(geocodingService.getCoordinate(command.getAddress()))
                .willReturn(coordinate);

        UpdateHubResponse response =
                updateHubService.updateHub(command);

        assertThat(response.getName())
                .isEqualTo("서울 허브");

        assertThat(response.getAddress())
                .isEqualTo("경기도 성남시");

        verify(geocodingService)
                .getCoordinate(command.getAddress());

        verify(refreshHubRouteUseCase)
                .refreshRoutesByHub(hub);
    }

    @Test
    @DisplayName("삭제된 허브 수정 시도 시 예외 발생")
    void updateHub_fail_deletedHub(){
        UUID managerId = UUID.randomUUID();
        UUID hubId = UUID.randomUUID();

        Hub hub = Hub.builder()
                .name("서울 허브")
                .address("서울")
                .latitude(37.1)
                .longitude(127.1)
                .managerId(managerId)
                .build();

        ReflectionTestUtils.setField(hub, "deletedAt", Instant.now());

        given(hubRepository.findById(hubId))
                .willReturn(Optional.of(hub));

        UpdateHubCommand command = UpdateHubCommand.builder()
                .id(hubId)
                .name("서울 허브")
                .address("경기도 성남시")
                .build();

        assertThatThrownBy(() -> updateHubService.updateHub(command))
                .isInstanceOf(ApiException.class)
                .hasMessage(ErrorResponseCode.HUB_ALREADY_DELETED.getMessage());

    }

    @Test
    @DisplayName("존재하지 않는 허브 수정 시도 시 예외 발생")
    void updateHub_fail_hubNotFound() {
        UUID hubId = UUID.randomUUID();

        given(hubRepository.findById(hubId))
                .willReturn(Optional.empty());

        UpdateHubCommand command = UpdateHubCommand.builder()
                .id(hubId)
                .name("서울 허브")
                .address("경기도 성남시")
                .build();

        assertThatThrownBy(() -> updateHubService.updateHub(command))
                .isInstanceOf(ApiException.class)
                .hasMessage(ErrorResponseCode.HUB_NOT_FOUND.getMessage());

        verify(hubRepository).findById(hubId);
        verifyNoInteractions(geocodingService);
    }

    @Test
    @DisplayName("주소가 변경되지 않으면 지오코딩을 호출하지 않는다")
    void updateHub_success_addressUnchanged_skipsGeocoding() {
        UUID hubId = UUID.randomUUID();
        UUID managerId = UUID.randomUUID();

        Hub hub = Hub.builder()
                .name("서울 허브")
                .address("서울특별시 송파구")
                .latitude(37.1)
                .longitude(127.1)
                .managerId(managerId)
                .build();

        given(hubRepository.findById(hubId))
                .willReturn(Optional.of(hub));

        UpdateHubCommand command = UpdateHubCommand.builder()
                .id(hubId)
                .name("새로운 이름")
                .address("서울특별시 송파구")
                .build();

        UpdateHubResponse response = updateHubService.updateHub(command);

        assertThat(response.getName()).isEqualTo("새로운 이름");
        assertThat(response.getAddress()).isEqualTo("서울특별시 송파구");
        assertThat(response.getLatitude()).isEqualTo(37.1);
        assertThat(response.getLongitude()).isEqualTo(127.1);

        verifyNoInteractions(geocodingService);
        verifyNoInteractions(refreshHubRouteUseCase);
    }

    @Test
    @DisplayName("이름과 담당자가 null이면 기존 값이 유지된다")
    void updateHub_success_partialUpdate_keepsExistingWhenFieldsNull() {
        UUID hubId = UUID.randomUUID();
        UUID originalManagerId = UUID.randomUUID();

        Hub hub = Hub.builder()
                .name("서울 허브")
                .address("서울특별시 송파구")
                .latitude(37.1)
                .longitude(127.1)
                .managerId(originalManagerId)
                .build();

        given(hubRepository.findById(hubId))
                .willReturn(Optional.of(hub));

        UpdateHubCommand command = UpdateHubCommand.builder()
                .id(hubId)
                .name(null)
                .address("경기도 성남시")
                .build();

        Coordinate coordinate = new Coordinate(37.222, 127.333);

        given(geocodingService.getCoordinate(command.getAddress()))
                .willReturn(coordinate);

        UpdateHubResponse response = updateHubService.updateHub(command);

        assertThat(response.getName()).isEqualTo("서울 허브");
        assertThat(response.getManagerId()).isEqualTo(originalManagerId);
        assertThat(response.getAddress()).isEqualTo("경기도 성남시");
        assertThat(response.getLatitude()).isEqualTo(37.222);
        assertThat(response.getLongitude()).isEqualTo(127.333);
    }

    @Test
    @DisplayName("수정된 허브 정보가 응답의 모든 필드에 매핑된다")
    void updateHub_success_fullResponseMapping() {
        UUID hubId = UUID.randomUUID();

        Hub hub = Hub.builder()
                .name("서울 허브")
                .address("서울")
                .latitude(37.1)
                .longitude(127.1)
                .build();

        ReflectionTestUtils.setField(hub, "id", hubId);

        given(hubRepository.findById(hubId))
                .willReturn(Optional.of(hub));

        UpdateHubCommand command = UpdateHubCommand.builder()
                .id(hubId)
                .name("부산 허브")
                .address("부산광역시")
                .build();

        Coordinate coordinate = new Coordinate(35.1, 129.0);

        given(geocodingService.getCoordinate(command.getAddress()))
                .willReturn(coordinate);

        UpdateHubResponse response = updateHubService.updateHub(command);

        assertThat(response.getId()).isEqualTo(hubId);
        assertThat(response.getName()).isEqualTo("부산 허브");
        assertThat(response.getAddress()).isEqualTo("부산광역시");
        assertThat(response.getLatitude()).isEqualTo(35.1);
        assertThat(response.getLongitude()).isEqualTo(129.0);
    }

    @Test
    @DisplayName("요청 주소가 null이면 기존 주소와 좌표를 유지하고 경로를 재계산하지 않는다")
    void updateHub_addressNull_keepsExistingAddress() {
        // given
        UUID hubId = UUID.randomUUID();
        UUID managerId = UUID.randomUUID();

        Hub hub = Hub.builder()
                .name("서울 허브")
                .address("서울특별시 송파구")
                .latitude(37.1)
                .longitude(127.1)
                .managerId(managerId)
                .build();

        given(hubRepository.findById(hubId))
                .willReturn(Optional.of(hub));

        UpdateHubCommand command = UpdateHubCommand.builder()
                .id(hubId)
                .name(null)
                .address(null)
                .build();

        // when
        UpdateHubResponse response =
                updateHubService.updateHub(command);

        // then
        assertThat(response.getAddress())
                .isEqualTo("서울특별시 송파구");
        assertThat(response.getLatitude()).isEqualTo(37.1);
        assertThat(response.getLongitude()).isEqualTo(127.1);

        verifyNoInteractions(geocodingService);
        verifyNoInteractions(refreshHubRouteUseCase);
    }

}