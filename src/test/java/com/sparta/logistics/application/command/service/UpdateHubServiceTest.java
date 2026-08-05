package com.sparta.logistics.application.command.service;

import com.sparta.logistics.application.command.dto.hub.UpdateHubCommand;
import com.sparta.logistics.application.command.dto.hub.UpdateHubResponse;
import com.sparta.logistics.application.common.dto.Coordinate;
import com.sparta.logistics.application.common.service.GeocodingService;
import com.sparta.logistics.common.code.ErrorResponseCode;
import com.sparta.logistics.common.exception.ApiException;
import com.sparta.logistics.domain.entity.Hub;
import com.sparta.logistics.domain.repository.HubRepository;
import org.junit.jupiter.api.DisplayName;
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

@ExtendWith(MockitoExtension.class)
class UpdateHubServiceTest {

    @Mock
    private HubRepository hubRepository;

    @Mock
    private GeocodingService geocodingService;

    @InjectMocks
    private UpdateHubService updateHubService;

    @Test
    @DisplayName("허브 수정 성공")
    void updateHub_success() {
        UUID hubId = UUID.randomUUID();
        UUID managerId = UUID.randomUUID();

        Hub hub = Hub.builder()
                .name("서울 허브")
                .address("서울")
                .latitude(37.1)
                .longitude(127.1)
                .managerId(managerId)
                .build();

        given(hubRepository.findById(hubId))
                .willReturn(Optional.of(hub));

        UpdateHubCommand command = UpdateHubCommand.builder()
                .id(hubId)
                .name("서울 허브")
                .address("경기도 성남시")
                .managerId(managerId)
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
                .managerId(managerId)
                .build();

        assertThatThrownBy(() -> updateHubService.updateHub(command))
                .isInstanceOf(ApiException.class)
                .hasMessage(ErrorResponseCode.HUB_ALREADY_DELETED.getMessage());

    }

}