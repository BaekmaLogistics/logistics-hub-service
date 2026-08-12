package com.sparta.logistics.presentation.command.request;

import com.sparta.logistics.application.command.dto.hubinventory.UpdateHubInventoryCommand;
import com.sparta.logistics.domain.model.UserRole;
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

    public UpdateHubInventoryCommand toCommand(
            UUID inventoryId,
            UUID requesterId,
            UserRole requesterRole
    ){
        return UpdateHubInventoryCommand.builder()
                .id(inventoryId)
                .quantity(quantity)
                .requesterId(requesterId)
                .requesterRole(requesterRole)
                .build();
    }
}
