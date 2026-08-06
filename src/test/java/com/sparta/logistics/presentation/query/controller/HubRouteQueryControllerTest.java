package com.sparta.logistics.presentation.query.controller;

import com.sparta.logistics.application.query.dto.HubRouteDetailResponse;
import com.sparta.logistics.application.query.usecase.HubRouteQueryUseCase;
import com.sparta.logistics.domain.entity.Hub;
import com.sparta.logistics.domain.entity.HubRoute;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@Tag("unit")
@WebMvcTest(HubRouteQueryController.class)
@AutoConfigureMockMvc(addFilters = false)
@TestPropertySource(properties = {
        "NAVER_MAP_URL=http://localhost"
})
class HubRouteQueryControllerTest {

    @MockitoBean
    private HubRouteQueryUseCase hubRouteQueryUseCase;

    @Test
    @DisplayName("허브 연결 목록 조회하기")
    void searchHubRoutes(){
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
    }
}