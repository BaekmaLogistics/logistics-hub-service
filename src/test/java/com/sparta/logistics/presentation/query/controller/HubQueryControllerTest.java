package com.sparta.logistics.presentation.query.controller;

import com.sparta.logistics.application.query.dto.HubDetailResponse;
import com.sparta.logistics.application.query.dto.HubSearchCondition;
import com.sparta.logistics.application.query.usecase.HubQueryUseCase;
import com.sparta.logistics.common.code.ErrorResponseCode;
import com.sparta.logistics.common.exception.ApiException;
import com.sparta.logistics.domain.entity.Hub;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Tag("unit")
@WebMvcTest(HubQueryController.class)
@TestPropertySource(properties = {
        "NAVER_MAP_URL=http://localhost"
})
class HubQueryControllerTest {

    @MockitoBean
    private HubQueryUseCase hubQueryUseCase;

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("허브 단건 조회 성공")
    void getHub_success() throws Exception{
        UUID hubId = UUID.randomUUID();

        HubDetailResponse response = HubDetailResponse.from(
                Hub.create(
                        "서울 허브",
                        "서울특별시 송파구",
                        37.5145,
                        127.1059
                )
        );

        when(hubQueryUseCase.getHub(hubId))
                .thenReturn(response);

        mockMvc.perform(get("/api/v1/hubs/{hubId}", hubId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("요청이 성공적으로 처리되었습니다."))
                .andExpect(jsonPath("$.data.name").value("서울 허브"))
                .andExpect(jsonPath("$.data.address").value("서울특별시 송파구"))
                .andExpect(jsonPath("$.data.latitude").value(37.5145))
                .andExpect(jsonPath("$.data.longitude").value(127.1059));

        verify(hubQueryUseCase).getHub(hubId);
    }

    @Test
    @DisplayName("존재하지 않는 허브 조회 시 예외를 발생시킨다.")
    void getHub_fail() throws Exception{
        UUID hubId = UUID.randomUUID();

        when(hubQueryUseCase.getHub(hubId))
                .thenThrow(new ApiException(ErrorResponseCode.HUB_NOT_FOUND));

        mockMvc.perform(get("/api/v1/hubs/{hubId}", hubId))
                .andExpect(status().isNotFound());


    }

    @Test
    @DisplayName("허브 목록 조회 성공")
    void search_hubs_success() throws Exception {
        HubDetailResponse response = HubDetailResponse.from(
                Hub.create(
                        "서울 허브",
                        "서울특별시 송파구",
                        37.5145,
                        127.1059
                )
        );

        Page<HubDetailResponse> page =
                new PageImpl<>(List.of(response));

        when(hubQueryUseCase.searchHubs(
                any(HubSearchCondition.class),
                any(Pageable.class)
        )).thenReturn(page);

        mockMvc.perform(get("/api/v1/hubs")
                        .param("name", "서울")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("요청이 성공적으로 처리되었습니다."))
                .andExpect(jsonPath("$.data.content[0].name").value("서울 허브"));

        verify(hubQueryUseCase).searchHubs(
                any(HubSearchCondition.class),
                any(Pageable.class)
        );
    }

    @Test
    @DisplayName("조회 결과 없으면 빈 목록 반환")
    void search_hubs_empty() throws Exception {
        Page<HubDetailResponse> page = Page.empty();

        when(hubQueryUseCase.searchHubs(
                any(HubSearchCondition.class),
                any(Pageable.class)
        )).thenReturn(page);

        mockMvc.perform(get("/api/v1/hubs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content").isEmpty());

        verify(hubQueryUseCase).searchHubs(
                any(HubSearchCondition.class),
                any(Pageable.class)
        );
    }
}