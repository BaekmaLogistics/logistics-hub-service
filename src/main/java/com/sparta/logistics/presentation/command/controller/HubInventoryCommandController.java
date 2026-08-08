package com.sparta.logistics.presentation.command.controller;

import com.sparta.logistics.application.command.dto.hubinventory.CreateHubInventoryResponse;
import com.sparta.logistics.application.command.dto.hubinventory.UpdateHubInventoryResponse;
import com.sparta.logistics.application.command.dto.hubinventory.UpdateSafetyStockResponse;
import com.sparta.logistics.application.command.usecase.CreateHubInventoryUseCase;
import com.sparta.logistics.application.command.usecase.UpdateHubInventoryUseCase;
import com.sparta.logistics.application.command.usecase.UpdateSafetyStockUseCase;
import com.sparta.logistics.common.code.GeneralResponseCode;
import com.sparta.logistics.presentation.command.request.CreateHubInventoryRequest;
import com.sparta.logistics.presentation.command.request.UpdateHubInventoryRequest;
import com.sparta.logistics.presentation.command.request.UpdateSafetyStockRequest;
import com.sparta.logistics.presentation.common.dto.response.GeneralResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/hub-inventories")
public class HubInventoryCommandController {

    private final CreateHubInventoryUseCase createHubInventoryUseCase;
    private final UpdateHubInventoryUseCase updateHubInventoryUseCase;
    private final UpdateSafetyStockUseCase updateSafetyStockUseCase;

    @PostMapping
    public ResponseEntity<GeneralResponse<CreateHubInventoryResponse>> createHubInventory(
            @Valid @RequestBody CreateHubInventoryRequest request
            ) {
        return GeneralResponse.toResponseEntity(
                GeneralResponseCode.CREATED,
                createHubInventoryUseCase.create(request.toCommand())
        );
    }

    @PatchMapping("/{inventoryId}")
    public ResponseEntity<GeneralResponse<UpdateHubInventoryResponse>> updateHubInventory(
            @PathVariable UUID inventoryId,
            @Valid @RequestBody UpdateHubInventoryRequest request
            ){
        UpdateHubInventoryResponse response =
                updateHubInventoryUseCase.update(
                        request.toCommand(inventoryId)
                );

        return GeneralResponse.toResponseEntity(
                GeneralResponseCode.OK,
                response
        );
    }

    @PatchMapping("/{inventoryId}/safety-stock")
    public ResponseEntity<GeneralResponse<UpdateSafetyStockResponse>> updateSafetyStock(
            @PathVariable UUID inventoryId,
            @Valid @RequestBody UpdateSafetyStockRequest request
            ){
        UpdateSafetyStockResponse response = updateSafetyStockUseCase.updateSafetyStock(request.toCommand(inventoryId));

        return GeneralResponse.toResponseEntity(
                GeneralResponseCode.OK,
                response
        );
    }
}
