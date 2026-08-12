package com.sparta.logistics.application.command.dto.hubinventory;

import com.sparta.logistics.domain.entity.HubInventory;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UpdateSafetyStockResponse {

    private final UUID inventoryId;
    private final Integer safetyStock;

    public static UpdateSafetyStockResponse from(HubInventory inventory){
        return new UpdateSafetyStockResponse(
                inventory.getId(),
                inventory.getSafetyStock()
        );
    }
}
