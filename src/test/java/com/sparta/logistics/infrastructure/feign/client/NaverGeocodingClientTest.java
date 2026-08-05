package com.sparta.logistics.infrastructure.feign.client;

import com.sparta.logistics.application.common.dto.Coordinate;
import com.sparta.logistics.application.common.service.GeocodingService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class NaverGeocodingClientTest {

    @Autowired
    private GeocodingService geocodingService;

    @Autowired
    ApplicationContext applicationContext;

    @Test
    @DisplayName("주소를 위도와 경도로 변환한다.")
    void geocode_success() {
        String address = "서울특별시 송파구 송파대로 55";

        Coordinate coordinate = geocodingService.getCoordinate(address);

        assertThat(coordinate).isNotNull();
        assertThat(coordinate.latitude()).isNotNull();
        assertThat(coordinate.longitude()).isNotNull();

        System.out.println(coordinate);
    }
}
