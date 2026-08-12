package com.sparta.logistics.application.command.service;

import com.sparta.logistics.application.command.dto.hub.AssignHubManagerCommand;
import com.sparta.logistics.application.command.dto.hub.AssignHubManagerResponse;
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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
class HubManagerAssignerTest {

    @InjectMocks
    private HubManagerAssigner hubManagerAssigner;

    @Mock
    private HubRepository hubRepository;

    @Test
    @DisplayName("허브 관리자를 배정한다.")
    void assignManager_success() {
        // given
        UUID hubId = UUID.randomUUID();
        UUID managerId = UUID.randomUUID();

        AssignHubManagerCommand command = AssignHubManagerCommand.builder()
                .hubId(hubId)
                .managerId(managerId)
                .build();

        Hub hub = createHub(hubId);

        given(hubRepository.findById(hubId))
                .willReturn(Optional.of(hub));

        given(hubRepository.existsByManagerIdAndDeletedAtIsNull(managerId))
                .willReturn(false);

        // when
        AssignHubManagerResponse response =
                hubManagerAssigner.assign(command);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getHubId()).isEqualTo(hubId);
        assertThat(response.getManagerId()).isEqualTo(managerId);
        assertThat(hub.getManagerId()).isEqualTo(managerId);

        verify(hubRepository).findById(hubId);
        verify(hubRepository)
                .existsByManagerIdAndDeletedAtIsNull(managerId);
    }

    @Test
    @DisplayName("허브가 존재하지 않으면 예외가 발생한다.")
    void assignManager_hubNotFound() {
        // given
        UUID hubId = UUID.randomUUID();
        UUID managerId = UUID.randomUUID();

        AssignHubManagerCommand command = AssignHubManagerCommand.builder()
                .hubId(hubId)
                .managerId(managerId)
                .build();

        given(hubRepository.findById(hubId))
                .willReturn(Optional.empty());

        // when
        ApiException exception = assertThrows(
                ApiException.class,
                () -> hubManagerAssigner.assign(command)
        );

        // then
        assertThat(exception.getResponseCode())
                .isEqualTo(ErrorResponseCode.HUB_NOT_FOUND);

        verify(hubRepository).findById(hubId);

        verify(
                hubRepository,
                never()
        ).existsByManagerIdAndDeletedAtIsNull(managerId);
    }

    @Test
    @DisplayName("삭제된 허브는 관리자를 배정할 수 없다.")
    void assignManager_deletedHub() {
        // given
        UUID hubId = UUID.randomUUID();
        UUID managerId = UUID.randomUUID();

        AssignHubManagerCommand command = AssignHubManagerCommand.builder()
                .hubId(hubId)
                .managerId(managerId)
                .build();

        Hub hub = createHub(hubId);
        ReflectionTestUtils.setField(hub, "deletedAt", Instant.now());

        given(hubRepository.findById(hubId))
                .willReturn(Optional.of(hub));

        // when
        ApiException exception = assertThrows(
                ApiException.class,
                () -> hubManagerAssigner.assign(command)
        );

        // then
        assertThat(exception.getResponseCode())
                .isEqualTo(ErrorResponseCode.HUB_ALREADY_DELETED);

        verify(hubRepository).findById(hubId);

        verify(
                hubRepository,
                never()
        ).existsByManagerIdAndDeletedAtIsNull(managerId);
    }

    @Test
    @DisplayName("현재 허브에 이미 배정된 관리자이면 예외가 발생한다.")
    void assignManager_alreadyAssigned() {
        // given
        UUID hubId = UUID.randomUUID();
        UUID managerId = UUID.randomUUID();

        AssignHubManagerCommand command = AssignHubManagerCommand.builder()
                .hubId(hubId)
                .managerId(managerId)
                .build();

        Hub hub = createHub(hubId);
        ReflectionTestUtils.setField(hub, "managerId", managerId);

        given(hubRepository.findById(hubId))
                .willReturn(Optional.of(hub));

        // when
        ApiException exception = assertThrows(
                ApiException.class,
                () -> hubManagerAssigner.assign(command)
        );

        // then
        assertThat(exception.getResponseCode())
                .isEqualTo(ErrorResponseCode.HUB_MANAGER_ALREADY_ASSIGNED);

        assertThat(hub.getManagerId()).isEqualTo(managerId);

        verify(hubRepository).findById(hubId);

        verify(
                hubRepository,
                never()
        ).existsByManagerIdAndDeletedAtIsNull(managerId);
    }

    @Test
    @DisplayName("이미 다른 활성 허브에 배정된 관리자는 배정할 수 없다.")
    void assignManager_fail_managerAlreadyAssignedToOtherHub() {
        // given
        UUID hubId = UUID.randomUUID();
        UUID managerId = UUID.randomUUID();

        AssignHubManagerCommand command = AssignHubManagerCommand.builder()
                .hubId(hubId)
                .managerId(managerId)
                .build();

        Hub hub = createHub(hubId);

        given(hubRepository.findById(hubId))
                .willReturn(Optional.of(hub));

        given(hubRepository.existsByManagerIdAndDeletedAtIsNull(managerId))
                .willReturn(true);

        // when & then
        assertThatThrownBy(() ->
                hubManagerAssigner.assign(command)
        )
                .isInstanceOf(ApiException.class)
                .hasMessage(
                        ErrorResponseCode
                                .HUB_MANAGER_ALREADY_ASSIGNED
                                .getMessage()
                );

        assertThat(hub.getManagerId()).isNull();

        verify(hubRepository).findById(hubId);
        verify(hubRepository)
                .existsByManagerIdAndDeletedAtIsNull(managerId);
    }

    private Hub createHub(UUID hubId) {
        Hub hub = Hub.builder()
                .name("서울특별시 센터")
                .address("서울특별시 송파구 송파대로 55")
                .latitude(37.514575)
                .longitude(127.105399)
                .build();

        ReflectionTestUtils.setField(hub, "id", hubId);

        return hub;
    }
}