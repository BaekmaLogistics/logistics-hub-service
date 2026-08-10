package com.sparta.logistics.application.query.usecase;

import com.sparta.logistics.application.query.dto.HubInventoryResponse;
import com.sparta.logistics.application.query.dto.HubInventorySearchCondition;
import com.sparta.logistics.domain.model.UserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface HubInventoryQueryUseCase {

    HubInventoryResponse getHubInventory(UUID inventoryId, UUID requesterId, UserRole requesterRole);

    Page<HubInventoryResponse> searchHubInventories(
            HubInventorySearchCondition condition,
            Pageable pageable,
            UUID requesterId,
            UserRole requesterRole
    );
}
