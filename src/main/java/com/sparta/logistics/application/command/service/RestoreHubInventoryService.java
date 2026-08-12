package com.sparta.logistics.application.command.service;

import com.sparta.logistics.application.command.usecase.RestoreHubInventoryUseCase;
import com.sparta.logistics.common.code.ErrorResponseCode;
import com.sparta.logistics.common.exception.ApiException;
import com.sparta.logistics.domain.entity.HubInventory;
import com.sparta.logistics.domain.entity.HubInventoryOperation;
import com.sparta.logistics.domain.repository.HubInventoryOperationRepository;
import com.sparta.logistics.domain.repository.HubInventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RestoreHubInventoryService{

    private final HubInventoryRepository hubInventoryRepository;
    private final HubInventoryOperationRepository hubInventoryOperationRepository;

    @Transactional
    public void restore(UUID orderId, UUID hubId, UUID productId){
        HubInventoryOperation operation = hubInventoryOperationRepository.findByOrderIdAndHub_IdAndProductId(orderId, hubId, productId)
                .orElseThrow(() -> new ApiException(ErrorResponseCode.INVENTORY_OPERATION_NOT_FOUND));

        if(operation.isRestored()){
            return;
        }

        HubInventory inventory = hubInventoryRepository.findByHubIdAndProductIdAndDeletedAtIsNull(hubId, productId)
                .orElseThrow(() -> new ApiException(ErrorResponseCode.HUB_INVENTORY_NOT_FOUND));

        inventory.increaseQuantity(operation.getQuantity());

        operation.restore();
    }
}
