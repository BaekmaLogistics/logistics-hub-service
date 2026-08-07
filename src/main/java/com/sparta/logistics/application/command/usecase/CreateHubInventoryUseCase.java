package com.sparta.logistics.application.command.usecase;

import com.sparta.logistics.application.command.dto.hubinventory.CreateHubInventoryCommand;
import com.sparta.logistics.application.command.dto.hubinventory.CreateHubInventoryResponse;

public interface CreateHubInventoryUseCase {

    CreateHubInventoryResponse create(
            CreateHubInventoryCommand command
    );
}
