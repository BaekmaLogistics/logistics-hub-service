package com.sparta.logistics.domain.repository;

import com.sparta.logistics.domain.entity.HubInventory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface HubInventoryRepositoryCustom {

    Page<HubInventory> search(
            UUID hubId,
            UUID productId,
            Pageable pageable
    );
}
