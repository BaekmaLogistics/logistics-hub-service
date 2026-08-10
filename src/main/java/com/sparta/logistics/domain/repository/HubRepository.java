package com.sparta.logistics.domain.repository;

import com.sparta.logistics.domain.entity.Hub;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface HubRepository extends JpaRepository<Hub, UUID>, HubRepositoryCustom {

    boolean existsByNameAndDeletedAtIsNull(String name);

    Optional<Hub> findByNameAndDeletedAtIsNull(String name);

    List<Hub> findAllByDeletedAtIsNull();

    Optional<Hub> findByIdAndDeletedAtIsNull(UUID id);
}
