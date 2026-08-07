package com.sparta.logistics.application.command.dto.hubinventory;

import com.sparta.logistics.domain.entity.HubInventory;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class CreateHubInventoryResponse {

    private UUID id;
    private UUID hubId;
    private UUID productId;
    private Integer quantity;
    private Integer safetyStock;

    public static CreateHubInventoryResponse from(HubInventory inventory) {
        return CreateHubInventoryResponse.builder()
                .id(inventory.getId())
                .hubId(inventory.getHubId())
                .productId(inventory.getProductId())
                .quantity(inventory.getQuantity())
                .safetyStock(inventory.getSafetyStock())
                .build();
    }
}
