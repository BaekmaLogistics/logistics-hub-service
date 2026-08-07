package com.sparta.logistics.application.command.dto.hubinventory;

import lombok.Builder;
import lombok.Getter;

import java.rmi.server.UID;
import java.util.UUID;

@Getter
@Builder
public class CreateHubInventoryCommand {

    private final UUID hubId;
    private final UUID productId;
    private final Integer quantity;
}
