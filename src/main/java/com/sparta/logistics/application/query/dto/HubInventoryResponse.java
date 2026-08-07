package com.sparta.logistics.application.query.dto;

import com.sparta.logistics.domain.entity.HubInventory;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class HubInventoryResponse {

    private final UUID id;
    private final UUID hubId;
    private final UUID productId;
    private final Integer quantity;
    private final Integer safetyStock;

    public static HubInventoryResponse from(HubInventory hubInventory){
        return new HubInventoryResponse(
                hubInventory.getId(),
                hubInventory.getHub().getId(),
                hubInventory.getProductId(),
                hubInventory.getQuantity(),
                hubInventory.getSafetyStock()
        );
    }
}
