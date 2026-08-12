package com.sparta.logistics.application.command.dto.hubinventory;

import com.sparta.logistics.domain.model.UserRole;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class UpdateSafetyStockCommand {

    private final UUID inventoryId;
    private final Integer safetyStock;

    private final UUID requesterId;
    private final UserRole requesterRole;
}
