package com.sparta.logistics.application.query.service;

import com.sparta.logistics.application.query.dto.HubInventoryResponse;
import com.sparta.logistics.application.query.dto.HubInventorySearchCondition;
import com.sparta.logistics.application.query.usecase.HubInventoryQueryUseCase;
import com.sparta.logistics.common.code.ErrorResponseCode;
import com.sparta.logistics.common.exception.ApiException;
import com.sparta.logistics.domain.entity.HubInventory;
import com.sparta.logistics.domain.repository.HubInventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class HubInventoryQueryService implements HubInventoryQueryUseCase {

    private final HubInventoryRepository hubInventoryRepository;

    @Override
    @Transactional(readOnly = true)
    public HubInventoryResponse getHubInventory(UUID inventoryId){
        HubInventory inventory = hubInventoryRepository.findById(inventoryId)
                .orElseThrow(() -> new ApiException(ErrorResponseCode.HUB_INVENTORY_NOT_FOUND));

        if(inventory.isDeleted()) {
            throw new ApiException(ErrorResponseCode.HUB_INVENTORY_NOT_FOUND);
        }

        return HubInventoryResponse.from(inventory);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<HubInventoryResponse> searchHubInventories(
            HubInventorySearchCondition condition,
            Pageable pageable
    ) {
        Page<HubInventory> hubInventories = hubInventoryRepository.search(condition.getHubId(), condition.getProductId(), pageable);
        return hubInventories.map(HubInventoryResponse::from);
    }
}
