package com.sparta.logistics.presentation.command.controller;

import com.sparta.logistics.application.command.dto.hubroute.CreateHubRouteCommand;
import com.sparta.logistics.application.command.dto.hubroute.CreateHubRouteResponse;
import com.sparta.logistics.application.command.dto.hubroute.UpdateHubRouteCommand;
import com.sparta.logistics.application.command.dto.hubroute.UpdateHubRouteResponse;
import com.sparta.logistics.application.command.usecase.CreateHubRouteUseCase;
import com.sparta.logistics.application.command.usecase.UpdateHubRouteUseCase;
import com.sparta.logistics.common.code.ErrorResponseCode;
import com.sparta.logistics.common.exception.ApiException;
import com.sparta.logistics.presentation.command.request.CreateHubRouteRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.Mockito.verify;

@Tag("unit")
@WebMvcTest(HubRouteCommandController.class)
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

    @Test
    @DisplayName("허브 연결 생성")
    void createHubRoute_success() throws Exception {
        // given
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

        when(createHubRouteUseCase.create(any(CreateHubRouteCommand.class)))
                .thenReturn(response);

        // when & then
        mockMvc.perform(post("/api/v1/hub-routes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.hubRouteId").value(hubRouteId.toString()))
                .andExpect(jsonPath("$.data.fromHubId").value(fromHubId.toString()))
                .andExpect(jsonPath("$.data.toHubId").value(toHubId.toString()))
                .andExpect(jsonPath("$.data.distance").value(35.8))
                .andExpect(jsonPath("$.data.duration").value(42));

        verify(createHubRouteUseCase).create(any(CreateHubRouteCommand.class));
    }

    @Test
    @DisplayName("허브 경로 수정 성공")
    void updateHubRoute_success() throws Exception {
        // given
        UUID hubRouteId = UUID.randomUUID();
        UUID fromHubId = UUID.randomUUID();
        UUID toHubId = UUID.randomUUID();

        UpdateHubRouteResponse response = UpdateHubRouteResponse.builder()
                .id(hubRouteId)
                .fromHubId(fromHubId)
                .toHubId(toHubId)
                .distance(250.0)
                .duration(180)
                .build();

        given(updateHubRouteUseCase.updateHubRoute(any(UpdateHubRouteCommand.class)))
                .willReturn(response);

        // when & then
        mockMvc.perform(
                        patch("/api/v1/hub-routes/{hubRouteId}", hubRouteId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                    {
                                      "fromHubId": "%s",
                                      "toHubId": "%s"
                                    }
                                    """.formatted(fromHubId, toHubId))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(hubRouteId.toString()))
                .andExpect(jsonPath("$.data.fromHubId").value(fromHubId.toString()))
                .andExpect(jsonPath("$.data.toHubId").value(toHubId.toString()))
                .andExpect(jsonPath("$.data.distance").value(250.0))
                .andExpect(jsonPath("$.data.duration").value(180));

        verify(updateHubRouteUseCase)
                .updateHubRoute(any(UpdateHubRouteCommand.class));
    }

    @Test
    @DisplayName("허브 경로 수정 요청이 비어있으면 HUB_0016 에러를 반환한다")
    void updateHubRoute_fail_emptyRequest() throws Exception {
        UUID hubRouteId = UUID.randomUUID();

        given(updateHubRouteUseCase.updateHubRoute(any(UpdateHubRouteCommand.class)))
                .willThrow(
                        new ApiException(
                                ErrorResponseCode.INVALID_HUB_ROUTE_UPDATE
                        )
                );

        mockMvc.perform(
                        patch("/api/v1/hub-routes/{hubRouteId}", hubRouteId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{}")
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("HUB_0016"));

        verify(updateHubRouteUseCase)
                .updateHubRoute(any(UpdateHubRouteCommand.class));
    }
}