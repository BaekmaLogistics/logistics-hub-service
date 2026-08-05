package com.sparta.logistics.application.command.service;

import com.sparta.logistics.application.command.dto.hub.DeleteHubCommand;
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
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class DeleteHubServiceTest {

    @Mock
    private HubRepository hubRepository;

    @InjectMocks
    private DeleteHubService deleteHubService;

    @Test
    @DisplayName("허브 삭제 성공")
    void deleteHub_success() {
        UUID hubId = UUID.randomUUID();
        UUID deletedBy = UUID.randomUUID();

        DeleteHubCommand command = DeleteHubCommand.builder()
                .id(hubId)
                .deletedBy(deletedBy)
                .build();

        Hub hub = Hub.builder()
                .name("서울 허브")
                .address("서울")
                .latitude(37.1)
                .longitude(127.1)
                .managerId(UUID.randomUUID())
                .build();

        given(hubRepository.findById(hubId))
                .willReturn(Optional.of(hub));

        deleteHubService.deleteHub(command);

        assertThat(hub.isDeleted()).isTrue();
    }

    @Test
    @DisplayName("존재하지 않는 허브 삭제")
    void deleteHub_fail_notFound() {
        DeleteHubCommand command = DeleteHubCommand.builder()
                .id(UUID.randomUUID())
                .deletedBy(UUID.randomUUID())
                .build();

        given(hubRepository.findById(any()))
                .willReturn(Optional.empty());

        assertThatThrownBy(() -> deleteHubService.deleteHub(command))
                .isInstanceOf(ApiException.class)
                .hasMessage(ErrorResponseCode.HUB_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("이미 삭제된 허브 삭제")
    void deleteHub_fail_alreadyDeleted() {

        // given
        UUID deletedBy = UUID.randomUUID();

        DeleteHubCommand command = DeleteHubCommand.builder()
                .id(UUID.randomUUID())
                .deletedBy(deletedBy)
                .build();

        Hub hub = Hub.builder()
                .name("서울 허브")
                .address("서울")
                .latitude(37.1)
                .longitude(127.1)
                .managerId(UUID.randomUUID())
                .build();

        hub.softDelete(UUID.randomUUID());

        given(hubRepository.findById(any()))
                .willReturn(Optional.of(hub));

        // when & then
        assertThatThrownBy(() -> deleteHubService.deleteHub(command))
                .isInstanceOf(ApiException.class)
                .hasMessage(ErrorResponseCode.HUB_ALREADY_DELETED.getMessage());
    }
}