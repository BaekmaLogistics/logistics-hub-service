package com.sparta.logistics.application.command.usecase;

import com.sparta.logistics.application.command.dto.hubinventory.UpdateHubInventoryCommand;
import com.sparta.logistics.application.command.dto.hubinventory.UpdateHubInventoryResponse;

public interface UpdateHubInventoryUseCase {

    UpdateHubInventoryResponse update(UpdateHubInventoryCommand command);
}
