package com.sparta.logistics.presentation.query.controller;

import com.sparta.logistics.application.query.dto.HubRouteDetailResponse;
import com.sparta.logistics.application.query.dto.ShortestPathResponse;
import com.sparta.logistics.application.query.usecase.FindShortestPathUseCase;
import com.sparta.logistics.application.query.usecase.HubRouteQueryUseCase;
import com.sparta.logistics.domain.entity.Hub;
import com.sparta.logistics.domain.entity.HubRoute;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Tag("unit")
@WebMvcTest(HubRouteQueryController.class)
@AutoConfigureMockMvc(addFilters = false)
@TestPropertySource(properties = {
        "NAVER_MAP_URL=http://localhost"
})
class HubRouteQueryControllerTest {

    @MockitoBean
    private HubRouteQueryUseCase hubRouteQueryUseCase;

    @MockitoBean
    private FindShortestPathUseCase findShortestPathUseCase;

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("허브 연결 목록 조회 성공")
    void searchHubRoutes() throws Exception {

        UUID fromHubId = UUID.randomUUID();
        UUID toHubId = UUID.randomUUID();

        Hub fromHub = Hub.builder()
                .name("서울")
                .build();
        ReflectionTestUtils.setField(fromHub, "id", fromHubId);

        Hub toHub = Hub.builder()
                .name("경기북부")
                .build();
        ReflectionTestUtils.setField(toHub, "id", toHubId);

        HubRoute hubRoute = HubRoute.builder()
                .fromHub(fromHub)
                .toHub(toHub)
                .distance(35.8)
                .duration(42)
                .build();

        ReflectionTestUtils.setField(hubRoute, "id", UUID.randomUUID());

        HubRouteDetailResponse response =
                HubRouteDetailResponse.from(hubRoute);

        Page<HubRouteDetailResponse> page =
                new PageImpl<>(List.of(response));

        when(hubRouteQueryUseCase.getHubRoutes(
                any(),
                any(Pageable.class)
        )).thenReturn(page);

        mockMvc.perform(get("/api/v1/hub-routes")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message")
                        .value("요청이 성공적으로 처리되었습니다."))
                .andExpect(jsonPath("$.data.content[0].fromHubName")
                        .value("서울"))
                .andExpect(jsonPath("$.data.content[0].toHubName")
                        .value("경기북부"))
                .andExpect(jsonPath("$.data.content[0].distance")
                        .value(35.8))
                .andExpect(jsonPath("$.data.content[0].duration")
                        .value(42));

        verify(hubRouteQueryUseCase).getHubRoutes(
                any(),
                any(Pageable.class)
        );
    }

    @Test
    @DisplayName("조회 결과가 없으면 빈 목록을 반환한다.")
    void searchHubRoutes_empty() throws Exception {

        when(hubRouteQueryUseCase.getHubRoutes(
                any(),
                any(Pageable.class)
        )).thenReturn(Page.empty());

        mockMvc.perform(get("/api/v1/hub-routes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content").isEmpty());

        verify(hubRouteQueryUseCase).getHubRoutes(
                any(),
                any(Pageable.class)
        );
    }

    @Test
    @DisplayName("최단 경로를 조회한다.")
    void findShortestPath() throws Exception {
        UUID fromHubId = UUID.randomUUID();
        UUID middleHubId = UUID.randomUUID();
        UUID toHubId = UUID.randomUUID();

        ShortestPathResponse response =
                new ShortestPathResponse(
                        List.of(fromHubId, middleHubId, toHubId),
                        120.5,
                        95
                );

        when(findShortestPathUseCase.findShortestPath(
                fromHubId,
                toHubId
        )).thenReturn(response);

        // when & then
        mockMvc.perform(get("/api/v1/hub-routes/shortest")
                        .param("fromHubId", fromHubId.toString())
                        .param("toHubId", toHubId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.hubIds[0]")
                        .value(fromHubId.toString()))
                .andExpect(jsonPath("$.data.hubIds[1]")
                        .value(middleHubId.toString()))
                .andExpect(jsonPath("$.data.hubIds[2]")
                        .value(toHubId.toString()))
                .andExpect(jsonPath("$.data.totalDistance")
                        .value(120.5))
                .andExpect(jsonPath("$.data.totalDuration")
                        .value(95));

        verify(findShortestPathUseCase)
                .findShortestPath(fromHubId, toHubId);
    }

    @Test
    @DisplayName("fromHubId가 없으면 400을 반환한다.")
    void fail_whenFromHubIdIsMissing() throws Exception {

        mockMvc.perform(get("/api/v1/hub-routes/shortest")
                        .param("toHubId", UUID.randomUUID().toString()))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("toHubId가 없으면 400을 반환한다.")
    void fail_whenToHubIdIsMissing() throws Exception {

        mockMvc.perform(get("/api/v1/hub-routes/shortest")
                        .param("fromHubId", UUID.randomUUID().toString()))
                .andExpect(status().isBadRequest());
    }
}