package com.sparta.logistics.domain.repository;

import com.sparta.logistics.domain.entity.HubInventory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface HubInventoryRepository extends JpaRepository<HubInventory, UUID> {

    boolean existsByHubIdAndProductIdAndDeletedAtIsNull(
            UUID hubId,
            UUID productId
    );
}
