package com.sparta.logistics.domain.repository;

import com.sparta.logistics.domain.entity.Hub;
import com.sparta.logistics.domain.entity.HubRoute;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface HubRouteRepository extends JpaRepository<HubRoute, UUID>, HubRouteRepositoryCustom {
    boolean existsByFromHubAndToHubAndDeletedAtIsNull(Hub fromHub, Hub toHub);

    boolean existsByFromHubAndToHubAndIdNotAndDeletedAtIsNull(Hub fromHub, Hub toHub, UUID id);

    List<HubRoute> findAllByDeletedAtIsNull();

    List<HubRoute> findAllActiveRoutesByHub(Hub hub);
}
