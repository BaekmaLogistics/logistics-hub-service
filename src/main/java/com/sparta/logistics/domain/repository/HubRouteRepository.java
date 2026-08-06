package com.sparta.logistics.domain.repository;

import com.sparta.logistics.domain.entity.Hub;
import com.sparta.logistics.domain.entity.HubRoute;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface HubRouteRepository extends JpaRepository<HubRoute, UUID>, HubRouteRepositoryCustom {
    boolean existsByFromHubAndToHub(Hub fromHub, Hub toHub);

    List<HubRoute> findAllByDeletedAtIsNull();
}
