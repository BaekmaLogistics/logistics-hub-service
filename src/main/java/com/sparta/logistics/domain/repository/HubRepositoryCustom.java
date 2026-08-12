package com.sparta.logistics.domain.repository;

import com.sparta.logistics.application.query.dto.HubSearchCondition;
import com.sparta.logistics.domain.entity.Hub;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface HubRepositoryCustom {
    Page<Hub> search(
            String name,
            String address,
            Pageable pageable
    );
}
