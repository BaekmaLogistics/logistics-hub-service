package com.sparta.logistics.application.command.service;

import com.sparta.logistics.application.command.dto.hubinventory.UpdateHubInventoryCommand;
import com.sparta.logistics.application.command.dto.hubinventory.UpdateHubInventoryResponse;
import com.sparta.logistics.application.command.usecase.UpdateHubInventoryUseCase;
import com.sparta.logistics.common.code.ErrorResponseCode;
import com.sparta.logistics.common.exception.ApiException;
import com.sparta.logistics.domain.entity.HubInventory;
import com.sparta.logistics.domain.repository.HubInventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateHubInventoryService implements UpdateHubInventoryUseCase {

    private final HubInventoryRepository hubInventoryRepository;

    @Override
    @Transactional
    public UpdateHubInventoryResponse update(UpdateHubInventoryCommand command){
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

        // TODO: RabbitMQ 연동 시
        // 정상 재고 -> 안전재고 이하로 전환된 경우 InventoryLowEvent 발행
        // boolean becameLowStock = !wasLowStock && inventory.isLowStock();

        return UpdateHubInventoryResponse.from(inventory);
    }
}
