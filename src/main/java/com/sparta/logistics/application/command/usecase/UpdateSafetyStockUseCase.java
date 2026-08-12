package com.sparta.logistics.application.command.usecase;

import com.sparta.logistics.application.command.dto.hubinventory.UpdateSafetyStockCommand;
import com.sparta.logistics.application.command.dto.hubinventory.UpdateSafetyStockResponse;

public interface UpdateSafetyStockUseCase {

    UpdateSafetyStockResponse updateSafetyStock(UpdateSafetyStockCommand command);
}
