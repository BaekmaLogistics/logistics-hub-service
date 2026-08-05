package com.sparta.logistics.infrastructure.feign.client;

import com.sparta.logistics.infrastructure.feign.config.NaverFeignConfig;
import com.sparta.logistics.infrastructure.feign.dto.direction.DirectionResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
        name = "naver-direction",
        url = "${naver.maps.url}",
        configuration = NaverFeignConfig.class
)
public interface NaverDirectionClient {

    @GetMapping("/map-direction/v1/driving")
    DirectionResponse getDirection(
            @RequestParam("start") String start,
            @RequestParam("goal") String goal,
            @RequestParam(value = "option", defaultValue = "trafast") String option
    );
}
