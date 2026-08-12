package com.sparta.logistics.infrastructure.feign.client;

import com.sparta.logistics.application.common.service.DirectionService;
import com.sparta.logistics.domain.entity.Hub;
import com.sparta.logistics.infrastructure.feign.dto.direction.RouteInfo;
import com.sparta.logistics.support.IntegrationTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@Tag("external")
class NaverDirectionClientTest extends IntegrationTestSupport {

    @Autowired
    private DirectionService directionService;


    @Test
    @DisplayName("출발지와 도착지 사이의 거리와 소요시간 조회")
    void get_route_success() {

        Hub fromHub = Hub.builder()
                .latitude(37.514575)
                .longitude(127.105399)
                .build();

        Hub toHub = Hub.builder()
                .latitude(37.658359)
                .longitude(126.832020)
                .build();

        RouteInfo routeInfo = directionService.getRoute(fromHub, toHub);

        assertThat(routeInfo).isNotNull();
        assertThat(routeInfo.getDistance()).isPositive();
        assertThat(routeInfo.getDuration()).isPositive();
    }
}