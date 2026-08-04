package com.sparta.logistics.infrastructure.feign.client;

import com.sparta.logistics.infrastructure.feign.config.NaverFeignConfig;
import com.sparta.logistics.infrastructure.feign.dto.gecoding.GeocodingResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
        name = "naver-geocoding",
        url = "${naver.maps.url}",
        configuration = NaverFeignConfig.class
)
public interface NaverGeocodingClient {

    @GetMapping("/map-geocode/v2/geocode")
    GeocodingResponse geocode(
            @RequestParam("query") String query
    );
}
