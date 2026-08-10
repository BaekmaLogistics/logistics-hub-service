package com.sparta.logistics.application.command.service;

import com.sparta.logistics.application.command.dto.hubinventory.UpdateHubInventoryCommand;
import com.sparta.logistics.application.command.dto.hubinventory.UpdateHubInventoryResponse;
import com.sparta.logistics.application.command.usecase.UpdateHubInventoryUseCase;
import com.sparta.logistics.application.event.InventoryLowEvent;
import com.sparta.logistics.common.code.ErrorResponseCode;
import com.sparta.logistics.common.exception.ApiException;
import com.sparta.logistics.domain.entity.HubInventory;
import com.sparta.logistics.domain.repository.HubInventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class UpdateHubInventoryService implements UpdateHubInventoryUseCase {

    private final HubInventoryRepository hubInventoryRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public UpdateHubInventoryResponse update(UpdateHubInventoryCommand command){
        if(command == null || command.getId() == null || command.getQuantity() == null){
            throw new ApiException(ErrorResponseCode.INVALID_REQUEST);
        }
        HubInventory inventory = hubInventoryRepository.findById(command.getId())
                .orElseThrow(() -> new ApiException(
                        ErrorResponseCode.HUB_INVENTORY_NOT_FOUND
                ));

        if(inventory.isDeleted()){
            throw new ApiException(
                    ErrorResponseCode.HUB_INVENTORY_ALREADY_DELETED
            );
        }

        boolean wasLowStock = inventory.isLowStock();;

        inventory.updateQuantity(command.getQuantity());

         boolean becameLowStock = !wasLowStock && inventory.isLowStock();

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

        return UpdateHubInventoryResponse.from(inventory);
    }
}
