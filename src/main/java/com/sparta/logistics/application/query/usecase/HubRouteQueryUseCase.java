package com.sparta.logistics.application.query.usecase;

import com.sparta.logistics.application.query.dto.HubRouteDetailResponse;
import com.sparta.logistics.application.query.dto.HubRouteSearchCondition;
import com.sparta.logistics.application.query.dto.ShortestPathResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface HubRouteQueryUseCase {

    Page<HubRouteDetailResponse> getHubRoutes(
            HubRouteSearchCondition condition,
            Pageable pageable
    );
}
