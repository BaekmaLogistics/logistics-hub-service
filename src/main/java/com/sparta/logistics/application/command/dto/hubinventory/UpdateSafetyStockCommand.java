package com.sparta.logistics.application.command.dto.hubinventory;

import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class UpdateSafetyStockCommand {

    private final UUID inventoryId;
    private final Integer safetyStock;
}
