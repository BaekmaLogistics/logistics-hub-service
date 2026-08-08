package com.sparta.logistics.domain.repository;

import com.sparta.logistics.domain.entity.Hub;
import com.sparta.logistics.domain.entity.HubInventoryOperation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface HubInventoryOperationRepository extends JpaRepository<HubInventoryOperation, UUID> {

    Optional<HubInventoryOperation> findByOrderIdAndHubAndProductId(
            UUID orderId,
            Hub hub,
            UUID productId
    );

    boolean existsByOrderIdAndHubAndProductId(
            UUID orderId,
            Hub hub,
            UUID productId
    );
}
