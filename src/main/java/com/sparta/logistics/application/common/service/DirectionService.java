package com.sparta.logistics.application.common.service;

import com.sparta.logistics.domain.entity.Hub;
import com.sparta.logistics.infrastructure.feign.dto.direction.RouteInfo;

public interface DirectionService {

    RouteInfo getRoute(
            Hub fromHub,
            Hub toHub
    );
}
