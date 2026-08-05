package com.sparta.logistics.domain.repository;

import com.sparta.logistics.domain.entity.Hub;
import org.reactivestreams.Publisher;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface HubRepository extends JpaRepository<Hub, UUID>, HubRepositoryCustom {

    boolean existsByName(String name);

    Optional<Hub> findByName(String name);
}
