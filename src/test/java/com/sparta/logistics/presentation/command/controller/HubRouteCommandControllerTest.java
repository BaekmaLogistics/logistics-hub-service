package com.sparta.logistics.presentation.command.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.logistics.application.command.dto.hubroute.*;
import com.sparta.logistics.application.command.usecase.CreateHubRouteUseCase;
import com.sparta.logistics.application.command.usecase.DeleteHubRouteUseCase;
import com.sparta.logistics.application.command.usecase.UpdateHubRouteUseCase;
import com.sparta.logistics.common.code.ErrorResponseCode;
import com.sparta.logistics.common.exception.ApiException;
import com.sparta.logistics.infrastructure.security.CustomAccessDeniedHandler;
import com.sparta.logistics.infrastructure.security.CustomAuthenticationEntryPoint;
import com.sparta.logistics.infrastructure.security.GatewayHeaderAuthenticationFilter;
import com.sparta.logistics.infrastructure.security.SecurityConfig;
import com.sparta.logistics.presentation.command.request.CreateHubRouteRequest;
import com.sparta.logistics.presentation.common.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Tag("unit")
@WebMvcTest(HubRouteCommandController.class)
@Import({
        SecurityConfig.class,
        GatewayHeaderAuthenticationFilter.class,
        CustomAuthenticationEntryPoint.class,
        CustomAccessDeniedHandler.class
})
@TestPropertySource(properties = {
        "NAVER_MAP_URL=http://localhost"
})
class HubRouteCommandControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CreateHubRouteUseCase createHubRouteUseCase;

    @MockitoBean
    private UpdateHubRouteUseCase updateHubRouteUseCase;

    @MockitoBean
    private DeleteHubRouteUseCase deleteHubRouteUseCase;

    @Test
    @DisplayName("MASTER는 허브 연결을 생성할 수 있다")
    void createHubRoute_success() throws Exception {
        // given
        UUID requesterId = UUID.randomUUID();
        UUID fromHubId = UUID.randomUUID();
        UUID toHubId = UUID.randomUUID();
        UUID hubRouteId = UUID.randomUUID();

        CreateHubRouteRequest request = CreateHubRouteRequest.builder()
                .fromHubId(fromHubId)
                .toHubId(toHubId)
                .build();

        CreateHubRouteResponse response = CreateHubRouteResponse.builder()
                .hubRouteId(hubRouteId)
                .fromHubId(fromHubId)
                .toHubId(toHubId)
                .distance(35.8)
                .duration(42)
                .build();

        given(createHubRouteUseCase.create(
                any(CreateHubRouteCommand.class)
        )).willReturn(response);

        // when & then
        mockMvc.perform(
                        post("/api/v1/hub-routes")
                                .header(
                                        "X-User-Id",
                                        requesterId.toString()
                                )
                                .header(
                                        "X-User-Role",
                                        "MASTER"
                                )
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(request)
                                )
                )
                .andExpect(status().isCreated())
                .andExpect(
                        jsonPath("$.data.hubRouteId")
                                .value(hubRouteId.toString())
                )
                .andExpect(
                        jsonPath("$.data.fromHubId")
                                .value(fromHubId.toString())
                )
                .andExpect(
                        jsonPath("$.data.toHubId")
                                .value(toHubId.toString())
                )
                .andExpect(
                        jsonPath("$.data.distance")
                                .value(35.8)
                )
                .andExpect(
                        jsonPath("$.data.duration")
                                .value(42)
                );

        verify(createHubRouteUseCase)
                .create(any(CreateHubRouteCommand.class));
    }

    @Test
    @DisplayName("MASTER는 허브 경로를 수정할 수 있다")
    void updateHubRoute_success() throws Exception {
        // given
        UUID requesterId = UUID.randomUUID();
        UUID hubRouteId = UUID.randomUUID();
        UUID fromHubId = UUID.randomUUID();
        UUID toHubId = UUID.randomUUID();

        UpdateHubRouteResponse response =
                UpdateHubRouteResponse.builder()
                        .id(hubRouteId)
                        .fromHubId(fromHubId)
                        .toHubId(toHubId)
                        .distance(250.0)
                        .duration(180)
                        .build();

        given(
                updateHubRouteUseCase.updateHubRoute(
                        any(UpdateHubRouteCommand.class)
                )
        ).willReturn(response);

        // when & then
        mockMvc.perform(
                        patch(
                                "/api/v1/hub-routes/{hubRouteId}",
                                hubRouteId
                        )
                                .header(
                                        "X-User-Id",
                                        requesterId.toString()
                                )
                                .header(
                                        "X-User-Role",
                                        "MASTER"
                                )
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "fromHubId": "%s",
                                          "toHubId": "%s"
                                        }
                                        """.formatted(
                                        fromHubId,
                                        toHubId
                                ))
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.data.id")
                                .value(hubRouteId.toString())
                )
                .andExpect(
                        jsonPath("$.data.fromHubId")
                                .value(fromHubId.toString())
                )
                .andExpect(
                        jsonPath("$.data.toHubId")
                                .value(toHubId.toString())
                )
                .andExpect(
                        jsonPath("$.data.distance")
                                .value(250.0)
                )
                .andExpect(
                        jsonPath("$.data.duration")
                                .value(180)
                );

        verify(updateHubRouteUseCase)
                .updateHubRoute(
                        any(UpdateHubRouteCommand.class)
                );
    }

    @Test
    @DisplayName("허브 경로 수정 요청이 비어있으면 HUB_ROUTE_0006 에러를 반환한다")
    void updateHubRoute_fail_emptyRequest() throws Exception {
        // given
        UUID requesterId = UUID.randomUUID();
        UUID hubRouteId = UUID.randomUUID();

        given(
                updateHubRouteUseCase.updateHubRoute(
                        any(UpdateHubRouteCommand.class)
                )
        ).willThrow(
                new ApiException(
                        ErrorResponseCode.INVALID_HUB_ROUTE_UPDATE
                )
        );

        // when & then
        mockMvc.perform(
                        patch(
                                "/api/v1/hub-routes/{hubRouteId}",
                                hubRouteId
                        )
                                .header(
                                        "X-User-Id",
                                        requesterId.toString()
                                )
                                .header(
                                        "X-User-Role",
                                        "MASTER"
                                )
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{}")
                )
                .andExpect(status().isBadRequest())
                .andExpect(
                        jsonPath("$.errorCode")
                                .value("HUB_ROUTE_0006")
                );

        verify(updateHubRouteUseCase)
                .updateHubRoute(
                        any(UpdateHubRouteCommand.class)
                );
    }

    @Test
    @DisplayName("MASTER는 허브 경로를 삭제할 수 있다")
    void deleteHubRoute_success() throws Exception {
        // given
        UUID requesterId = UUID.randomUUID();
        UUID hubRouteId = UUID.randomUUID();

        // when & then
        mockMvc.perform(
                        delete(
                                "/api/v1/hub-routes/{hubRouteId}",
                                hubRouteId
                        )
                                .header(
                                        "X-User-Id",
                                        requesterId.toString()
                                )
                                .header(
                                        "X-User-Role",
                                        "MASTER"
                                )
                )
                .andExpect(status().isOk());

        ArgumentCaptor<DeleteHubRouteCommand> captor =
                ArgumentCaptor.forClass(
                        DeleteHubRouteCommand.class
                );

        verify(deleteHubRouteUseCase)
                .deleteHubRoute(captor.capture());

        DeleteHubRouteCommand command = captor.getValue();

        assertThat(command.getId())
                .isEqualTo(hubRouteId);

        assertThat(command.getDeletedBy())
                .isEqualTo(requesterId);
    }

    @Test
    @DisplayName("HUB_MANAGER는 허브 연결을 생성할 수 없다")
    void createHubRoute_fail_forbidden() throws Exception {
        // given
        UUID requesterId = UUID.randomUUID();
        UUID fromHubId = UUID.randomUUID();
        UUID toHubId = UUID.randomUUID();

        CreateHubRouteRequest request =
                CreateHubRouteRequest.builder()
                        .fromHubId(fromHubId)
                        .toHubId(toHubId)
                        .build();

        // when & then
        mockMvc.perform(
                        post("/api/v1/hub-routes")
                                .header(
                                        "X-User-Id",
                                        requesterId.toString()
                                )
                                .header(
                                        "X-User-Role",
                                        "HUB_MANAGER"
                                )
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(request)
                                )
                )
                .andExpect(status().isForbidden());

        verifyNoInteractions(createHubRouteUseCase);
    }
}