package com.sparta.logistics.application.command.service;

import com.sparta.logistics.application.command.dto.hub.AssignHubManagerCommand;
import com.sparta.logistics.application.command.dto.hub.AssignHubManagerResponse;
import com.sparta.logistics.application.port.UserReader;
import com.sparta.logistics.common.code.ErrorResponseCode;
import com.sparta.logistics.common.exception.ApiException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
class AssignHubManagerServiceTest {

    @InjectMocks
    private AssignHubManagerService assignHubManagerService;

    @Mock
    private UserReader userReader;

    @Mock
    private HubManagerAssigner hubManagerAssigner;

    @Test
    @DisplayName("HUB_MANAGER 권한을 가진 사용자를 허브 관리자로 배정한다.")
    void assignManager_success() {
        // given
        UUID hubId = UUID.randomUUID();
        UUID managerId = UUID.randomUUID();

        AssignHubManagerCommand command = AssignHubManagerCommand.builder()
                .hubId(hubId)
                .managerId(managerId)
                .build();

        UserReader.UserInfo user = new UserReader.UserInfo(
                managerId,
                "HUB_MANAGER",
                null,
                null
        );

        AssignHubManagerResponse expectedResponse =
                AssignHubManagerResponse.builder()
                        .hubId(hubId)
                        .managerId(managerId)
                        .build();

        given(userReader.getUser(managerId))
                .willReturn(user);

        given(hubManagerAssigner.assign(command))
                .willReturn(expectedResponse);

        // when
        AssignHubManagerResponse response =
                assignHubManagerService.assign(command);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getHubId()).isEqualTo(hubId);
        assertThat(response.getManagerId()).isEqualTo(managerId);

        verify(userReader).getUser(managerId);
        verify(hubManagerAssigner).assign(command);
    }

    @Test
    @DisplayName("HUB_MANAGER 권한이 아닌 사용자는 허브 관리자로 배정할 수 없다.")
    void assignManager_fail_whenUserIsNotHubManager() {
        // given
        UUID hubId = UUID.randomUUID();
        UUID managerId = UUID.randomUUID();

        AssignHubManagerCommand command = AssignHubManagerCommand.builder()
                .hubId(hubId)
                .managerId(managerId)
                .build();

        given(userReader.getUser(managerId))
                .willReturn(new UserReader.UserInfo(
                        managerId,
                        "COMPANY_MANAGER",
                        null,
                        UUID.randomUUID()
                ));

        // when
        ApiException exception = assertThrows(
                ApiException.class,
                () -> assignHubManagerService.assign(command)
        );

        // then
        assertThat(exception.getResponseCode())
                .isEqualTo(ErrorResponseCode.HUB_MANAGER_ROLE_REQUIRED);

        verify(userReader).getUser(managerId);
        verify(hubManagerAssigner, never()).assign(command);
    }
}