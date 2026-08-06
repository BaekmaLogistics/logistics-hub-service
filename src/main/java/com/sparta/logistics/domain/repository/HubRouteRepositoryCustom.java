package com.sparta.logistics.domain.repository;

import com.sparta.logistics.application.query.dto.HubRouteSearchCondition;
import com.sparta.logistics.domain.entity.HubRoute;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface HubRouteRepositoryCustom {

    Page<HubRoute> search(
            String keyword,
            UUID fromHubId,
            UUID toHubId,
            Pageable pageable
    );
}
