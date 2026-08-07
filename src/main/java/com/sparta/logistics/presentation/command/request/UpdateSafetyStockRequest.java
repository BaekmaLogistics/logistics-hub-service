package com.sparta.logistics.presentation.command.request;

import com.sparta.logistics.application.command.dto.hubinventory.UpdateSafetyStockCommand;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
public class UpdateSafetyStockRequest {

    @NotNull
    @PositiveOrZero
    private Integer safetyStock;

    public UpdateSafetyStockCommand toCommand(UUID inventoryId) {
        return UpdateSafetyStockCommand.builder()
                .inventoryId(inventoryId)
                .safetyStock(safetyStock)
                .build();
    }
}
