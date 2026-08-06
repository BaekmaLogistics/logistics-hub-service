package com.sparta.logistics.application.query.service;

import com.sparta.logistics.application.query.dto.HubRouteDetailResponse;
import com.sparta.logistics.application.query.dto.HubRouteSearchCondition;
import com.sparta.logistics.application.query.usecase.HubRouteQueryUseCase;
import com.sparta.logistics.domain.repository.HubRouteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HubRouteQueryService implements HubRouteQueryUseCase {

    private final HubRouteRepository hubRouteRepository;

    @Override
    public Page<HubRouteDetailResponse> getHubRoutes(
            HubRouteSearchCondition condition,
            Pageable pageable
    ){
        return hubRouteRepository.search(condition, pageable)
                .map(HubRouteDetailResponse::from);
    }
}
