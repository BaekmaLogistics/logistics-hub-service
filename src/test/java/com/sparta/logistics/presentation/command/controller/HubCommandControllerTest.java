package com.sparta.logistics.presentation.command.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.logistics.application.command.dto.hub.CreateHubCommand;
import com.sparta.logistics.application.command.dto.hub.CreateHubResponse;
import com.sparta.logistics.application.command.usecase.CreateHubUseCase;
import com.sparta.logistics.common.code.ErrorResponseCode;
import com.sparta.logistics.common.exception.ApiException;
import com.sparta.logistics.presentation.command.request.CreateHubRequest;
import com.sparta.logistics.presentation.common.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(HubCommandController.class)
@Import(GlobalExceptionHandler.class)
class HubCommandControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    CreateHubUseCase createHubUseCase;

    @Test
    @DisplayName("허브 생성 성공")
    void createHub_success() throws Exception {
        UUID managerId = UUID.randomUUID();
        UUID hubId = UUID.randomUUID();

        CreateHubRequest request = CreateHubRequest.builder()
                .name("서울 허브")
                .address("서울특별시 송파구 송파대로 55")
                .managerId(managerId)
                .build();

        CreateHubResponse response = CreateHubResponse.builder()
                .id(hubId)
                .name("서울 허브")
                .address("서울특별시 송파구 송파대로 55")
                .latitude(37.474154)
                .longitude(127.123906)
                .managerId(managerId)
                .build();

        when(createHubUseCase.createHub(any(CreateHubCommand.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/v1/hubs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message")
                        .value("성공적으로 생성되었습니다."))
                .andExpect(jsonPath("$.data.name")
                        .value("서울 허브"))
                .andExpect(jsonPath("$.data.address")
                        .value("서울특별시 송파구 송파대로 55"));

        verify(createHubUseCase).createHub(any(CreateHubCommand.class));
    }

    @Test
    @DisplayName("허브명이 없으면 Validation 실패")
    void createHub_fail_nameBlank() throws Exception {

        UUID managerId = UUID.randomUUID();

        CreateHubRequest request = CreateHubRequest.builder()
                .name("")
                .address("서울특별시 송파구 송파대로 55")
                .managerId(managerId)
                .build();

        mockMvc.perform(post("/api/v1/hubs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(createHubUseCase);
    }

    @Test
    @DisplayName("이미 존재하는 허브명")
    void createHub_fail_duplicate() throws Exception {

        UUID managerId = UUID.randomUUID();

        CreateHubRequest request = CreateHubRequest.builder()
                .name("서울 허브")
                .address("서울특별시 송파구 송파대로 55")
                .managerId(managerId)
                .build();

        when(createHubUseCase.createHub(any(CreateHubCommand.class)))
                .thenThrow(new ApiException(ErrorResponseCode.HUB_ALREADY_EXISTS));

        mockMvc.perform(post("/api/v1/hubs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());

        verify(createHubUseCase).createHub(any(CreateHubCommand.class));
    }
}