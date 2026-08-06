package com.sparta.logistics.domain.repository;

import com.sparta.logistics.application.query.dto.HubRouteSearchCondition;
import com.sparta.logistics.domain.entity.HubRoute;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface HubRouteRepositoryCustom {

    Page<HubRoute> search(
            HubRouteSearchCondition condition,
            Pageable pageable
    );
}
