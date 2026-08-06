package com.sparta.logistics.application.query.service;

import com.sparta.logistics.application.query.dto.HubRouteDetailResponse;
import com.sparta.logistics.application.query.dto.HubRouteSearchCondition;
import com.sparta.logistics.domain.entity.Hub;
import com.sparta.logistics.domain.entity.HubRoute;
import com.sparta.logistics.domain.repository.HubRouteRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
class HubRouteQueryServiceTest {

    @InjectMocks
    private HubRouteQueryService hubRouteQueryService;

    @Mock
    private HubRouteRepository hubRouteRepository;

    @Test
    @DisplayName("허브 연결 목록을 조회한다.")
    void getHubRoutes(){
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

        Page<HubRoute> page =
                new PageImpl<>(List.of(hubRoute));

        when(hubRouteRepository.search(any(), any(), any(), any()))
                .thenReturn(page);

        Page<HubRouteDetailResponse> result =
                hubRouteQueryService.getHubRoutes(
                        new HubRouteSearchCondition(),
                        PageRequest.of(0, 10)
                );

        assertThat(result.getContent()).hasSize(1);

        HubRouteDetailResponse response =
                result.getContent().get(0);

        assertThat(response.getFromHubName())
                .isEqualTo("서울");

        assertThat(response.getToHubName())
                .isEqualTo("경기북부");

        assertThat(response.getDistance())
                .isEqualTo(35.8);

        assertThat(response.getDuration())
                .isEqualTo(42);

        verify(hubRouteRepository)
                .search(any(), any(), any(), any());
    }

}