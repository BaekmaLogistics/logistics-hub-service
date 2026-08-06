package com.sparta.logistics.domain.repository;

import com.sparta.logistics.domain.entity.HubRoute;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface HubRouteRepository extends JpaRepository<HubRoute, UUID> {
    boolean existsByFromHubIdAndToHubId(UUID fromHubId, UUID toHubId);
}
