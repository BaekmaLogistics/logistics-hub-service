package com.sparta.logistics.application.query.service;

import com.sparta.logistics.application.query.dto.HubDetailResponse;
import com.sparta.logistics.application.query.dto.HubSearchCondition;
import com.sparta.logistics.application.query.usecase.HubQueryUseCase;
import com.sparta.logistics.common.code.ErrorResponseCode;
import com.sparta.logistics.common.exception.ApiException;
import com.sparta.logistics.domain.entity.Hub;
import com.sparta.logistics.domain.repository.HubRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class HubQueryService implements HubQueryUseCase {

    private final HubRepository hubRepository;

    @Transactional(readOnly = true)
    @Override
    public HubDetailResponse getHub(UUID hubId) {
        Hub hub = hubRepository.findById(hubId)
                .orElseThrow(() -> new ApiException(ErrorResponseCode.HUB_NOT_FOUND));

        return HubDetailResponse.from(hub);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<HubDetailResponse> searchHubs(
            HubSearchCondition condition,
            Pageable pageable
    ) {
        Page<Hub> hubs = hubRepository.search(
                condition.getName(),
                condition.getAddress(),
                pageable
        );

        return hubs.map(HubDetailResponse::from);
    }
}
