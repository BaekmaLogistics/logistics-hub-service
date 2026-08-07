package com.sparta.logistics.presentation.command.request;

import com.sparta.logistics.application.command.dto.hubinventory.UpdateHubInventoryCommand;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
public class UpdateHubInventoryRequest {

    @NotNull
    @PositiveOrZero
    private Integer quantity;

    public UpdateHubInventoryCommand toCommand(UUID inventoryId){
        return UpdateHubInventoryCommand.builder()
                .id(inventoryId)
                .quantity(quantity)
                .build();
    }
}
