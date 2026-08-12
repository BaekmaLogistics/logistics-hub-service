package com.sparta.logistics.application.query.usecase;

import com.sparta.logistics.application.query.dto.HubDetailResponse;
import com.sparta.logistics.application.query.dto.HubSearchCondition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface HubQueryUseCase {

    HubDetailResponse getHub(UUID hubId);

    Page<HubDetailResponse> searchHubs(
            HubSearchCondition condition,
            Pageable pageable
    );
}
