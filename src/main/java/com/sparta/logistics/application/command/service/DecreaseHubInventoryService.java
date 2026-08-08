package com.sparta.logistics.application.command.service;

import com.sparta.logistics.application.command.usecase.DecreaseHubInventoryUseCase;
import com.sparta.logistics.common.code.ErrorResponseCode;
import com.sparta.logistics.common.exception.ApiException;
import com.sparta.logistics.domain.entity.HubInventory;
import com.sparta.logistics.domain.repository.HubInventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DecreaseHubInventoryService{

    private final HubInventoryRepository hubInventoryRepository;

    @Transactional
    public void decrease(UUID hubId, UUID productId, int quantity){
        HubInventory inventory = hubInventoryRepository.findByHubIdAndProductIdAndDeletedAtIsNull(hubId, productId)
                .orElseThrow(() -> new ApiException(ErrorResponseCode.HUB_INVENTORY_NOT_FOUND));

        inventory.decreaseQuantity(quantity);
    }
}
