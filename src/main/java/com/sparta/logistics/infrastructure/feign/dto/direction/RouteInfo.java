package com.sparta.logistics.infrastructure.feign.dto.direction;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class RouteInfo {

    private final Double distance;

    private final Integer duration;
}
