package com.sparta.logistics.application.command.service;

import com.sparta.logistics.application.command.usecase.DecreaseHubInventoryUseCase;
import com.sparta.logistics.application.event.InventoryLowEvent;
import com.sparta.logistics.common.code.ErrorResponseCode;
import com.sparta.logistics.common.exception.ApiException;
import com.sparta.logistics.domain.entity.HubInventory;
import com.sparta.logistics.domain.entity.HubInventoryOperation;
import com.sparta.logistics.domain.repository.HubInventoryOperationRepository;
import com.sparta.logistics.domain.repository.HubInventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DecreaseHubInventoryService{

    private final HubInventoryRepository hubInventoryRepository;
    private final HubInventoryOperationRepository hubInventoryOperationRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public void decrease(UUID orderId, UUID hubId, UUID productId, int quantity){
        HubInventory inventory = hubInventoryRepository.findByHubIdAndProductIdAndDeletedAtIsNull(hubId, productId)
                .orElseThrow(() -> new ApiException(ErrorResponseCode.HUB_INVENTORY_NOT_FOUND));

        Optional<HubInventoryOperation> existingOperation = hubInventoryOperationRepository.findByOrderIdAndHub_IdAndProductId(orderId, hubId, productId);

        if(existingOperation.isPresent()){
            HubInventoryOperation existedOperation = existingOperation.get();

            if (!existedOperation.getQuantity().equals(quantity)) {
                throw new ApiException(
                        ErrorResponseCode.INVENTORY_IDEMPOTENCY_CONFLICT
                );
            }

            return;
        }

        boolean wasLowStock = inventory.isLowStock();

        inventory.decreaseQuantity(quantity);

        boolean becameLowStock = !wasLowStock && inventory.isLowStock();

        HubInventoryOperation operation = HubInventoryOperation.builder()
                .orderId(orderId)
                .hub(inventory.getHub())
                .productId(productId)
                .quantity(quantity)
                .build();

        hubInventoryOperationRepository.save(operation);

        if(becameLowStock){
            eventPublisher.publishEvent(
                    new InventoryLowEvent(
                            inventory.getId(),
                            inventory.getHub().getId(),
                            inventory.getProductId(),
                            inventory.getQuantity(),
                            inventory.getSafetyStock(),
                            Instant.now()
                    )
            );
        }
    }
}
