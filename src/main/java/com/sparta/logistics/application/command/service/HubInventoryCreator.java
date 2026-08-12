package com.sparta.logistics.application.command.service;

import com.sparta.logistics.application.command.dto.hubinventory.CreateHubInventoryCommand;
import com.sparta.logistics.application.command.dto.hubinventory.CreateHubInventoryResponse;
import com.sparta.logistics.application.event.InventoryLowEvent;
import com.sparta.logistics.common.code.ErrorResponseCode;
import com.sparta.logistics.common.exception.ApiException;
import com.sparta.logistics.domain.entity.Hub;
import com.sparta.logistics.domain.entity.HubInventory;
import com.sparta.logistics.domain.repository.HubInventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class HubInventoryCreator {

    private final HubInventoryRepository hubInventoryRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public CreateHubInventoryResponse create(
            Hub hub,
            CreateHubInventoryCommand command
    ) {
        if (hubInventoryRepository.existsByHubAndProductIdAndDeletedAtIsNull(
                hub,
                command.getProductId()
        )) {
            throw new ApiException(
                    ErrorResponseCode.HUB_INVENTORY_ALREADY_EXISTS
            );
        }

        HubInventory inventory = HubInventory.builder()
                .hub(hub)
                .productId(command.getProductId())
                .quantity(command.getQuantity())
                .build();

        HubInventory savedInventory =
                hubInventoryRepository.save(inventory);

        if (savedInventory.isLowStock()) {
            eventPublisher.publishEvent(
                    new InventoryLowEvent(
                            savedInventory.getId(),
                            savedInventory.getHub().getId(),
                            savedInventory.getProductId(),
                            savedInventory.getQuantity(),
                            savedInventory.getSafetyStock(),
                            Instant.now()
                    )
            );
        }

        return CreateHubInventoryResponse.from(savedInventory);
    }
}