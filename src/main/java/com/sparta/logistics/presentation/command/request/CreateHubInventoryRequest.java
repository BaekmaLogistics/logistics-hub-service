package com.sparta.logistics.presentation.command.request;

import com.sparta.logistics.application.command.dto.hubinventory.CreateHubInventoryCommand;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
public class CreateHubInventoryRequest {

    @NotNull
    private UUID hubId;

    @NotNull
    private UUID productId;

    @NotNull
    @PositiveOrZero
    private Integer quantity;

    public CreateHubInventoryCommand toCommand() {
        return CreateHubInventoryCommand.builder()
                .hubId(hubId)
                .productId(productId)
                .quantity(quantity)
                .build();
    }
}
