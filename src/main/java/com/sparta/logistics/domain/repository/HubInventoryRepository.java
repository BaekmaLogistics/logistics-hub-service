package com.sparta.logistics.domain.repository;

import com.sparta.logistics.domain.entity.Hub;
import com.sparta.logistics.domain.entity.HubInventory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface HubInventoryRepository extends JpaRepository<HubInventory, UUID>, HubInventoryRepositoryCustom {

    boolean existsByHubAndProductIdAndDeletedAtIsNull(
            Hub hub,
            UUID productId
    );

    Optional<HubInventory> findByHubIdAndProductIdAndDeletedAtIsNull(UUID hubId, UUID productId);
}
