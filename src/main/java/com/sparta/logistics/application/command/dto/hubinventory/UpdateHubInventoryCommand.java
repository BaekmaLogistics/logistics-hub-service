package com.sparta.logistics.application.command.dto.hubinventory;

import com.sparta.logistics.domain.model.UserRole;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class UpdateHubInventoryCommand {

    private final UUID id;
    private final Integer quantity;

    private final UUID requesterId;
    private final UserRole requesterRole;
}
