package com.sparta.logistics.application.query.usecase;

import com.sparta.logistics.application.query.dto.HubRouteDetailResponse;
import com.sparta.logistics.application.query.dto.HubRouteSearchCondition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface HubRouteQueryUseCase {

    Page<HubRouteDetailResponse> getHubRoutes(
            HubRouteSearchCondition condition,
            Pageable pageable
    );
}
