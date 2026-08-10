package com.sparta.logistics.application.query.service;

import com.sparta.logistics.application.common.validator.HubAccessValidator;
import com.sparta.logistics.application.query.dto.HubInventoryResponse;
import com.sparta.logistics.application.query.dto.HubInventorySearchCondition;
import com.sparta.logistics.application.query.usecase.HubInventoryQueryUseCase;
import com.sparta.logistics.common.code.ErrorResponseCode;
import com.sparta.logistics.common.exception.ApiException;
import com.sparta.logistics.domain.entity.Hub;
import com.sparta.logistics.domain.entity.HubInventory;
import com.sparta.logistics.domain.model.UserRole;
import com.sparta.logistics.domain.repository.HubInventoryRepository;
import com.sparta.logistics.domain.repository.HubRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class HubInventoryQueryService implements HubInventoryQueryUseCase {

    private final HubRepository hubRepository;
    private final HubInventoryRepository hubInventoryRepository;
    private final HubAccessValidator hubAccessValidator;

    @Override
    @Transactional(readOnly = true)
    public HubInventoryResponse getHubInventory(UUID inventoryId, UUID requesterId, UserRole requesterRole){
        HubInventory inventory = hubInventoryRepository.findById(inventoryId)
                .orElseThrow(() -> new ApiException(ErrorResponseCode.HUB_INVENTORY_NOT_FOUND));

        if(inventory.isDeleted()) {
            throw new ApiException(ErrorResponseCode.HUB_INVENTORY_NOT_FOUND);
        }

        hubAccessValidator.validate(
                inventory.getHub(),
                requesterId,
                requesterRole
        );

        return HubInventoryResponse.from(inventory);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<HubInventoryResponse> searchHubInventories(
            HubInventorySearchCondition condition,
            Pageable pageable,
            UUID requesterId,
            UserRole requesterRole
    ) {
        UUID hubId = condition.getHubId();

        if(requesterRole == UserRole.HUB_MANAGER){
            Hub managedHub = hubRepository.findByManagerIdAndDeletedAtIsNull(requesterId)
                    .orElseThrow(() ->
                            new ApiException(ErrorResponseCode.HUB_ACCESS_DENIED)
                    );

            if (hubId != null && !hubId.equals(managedHub.getId())) {
                throw new ApiException(ErrorResponseCode.HUB_ACCESS_DENIED);
            }

            hubId = managedHub.getId();
        } else if(requesterRole != UserRole.MASTER){
            throw new ApiException(ErrorResponseCode.HUB_ACCESS_DENIED);
        }

        Page<HubInventory> hubInventories = hubInventoryRepository.search(hubId, condition.getProductId(), pageable);
        return hubInventories.map(HubInventoryResponse::from);
    }
}
