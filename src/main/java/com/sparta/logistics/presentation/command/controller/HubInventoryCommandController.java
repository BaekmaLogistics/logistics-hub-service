package com.sparta.logistics.presentation.command.controller;

import com.sparta.logistics.application.command.dto.hubinventory.CreateHubInventoryResponse;
import com.sparta.logistics.application.command.dto.hubinventory.UpdateHubInventoryResponse;
import com.sparta.logistics.application.command.dto.hubinventory.UpdateSafetyStockResponse;
import com.sparta.logistics.application.command.usecase.CreateHubInventoryUseCase;
import com.sparta.logistics.application.command.usecase.UpdateHubInventoryUseCase;
import com.sparta.logistics.application.command.usecase.UpdateSafetyStockUseCase;
import com.sparta.logistics.common.code.GeneralResponseCode;
import com.sparta.logistics.domain.model.UserRole;
import com.sparta.logistics.presentation.command.request.CreateHubInventoryRequest;
import com.sparta.logistics.presentation.command.request.UpdateHubInventoryRequest;
import com.sparta.logistics.presentation.command.request.UpdateSafetyStockRequest;
import com.sparta.logistics.presentation.common.dto.response.GeneralResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
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
    @PreAuthorize("hasAnyRole('MASTER', 'HUB_MANAGER')")
    public ResponseEntity<GeneralResponse<CreateHubInventoryResponse>> createHubInventory(
            @Valid @RequestBody CreateHubInventoryRequest request,
            Authentication authentication
            ) {
        UUID requesterId = (UUID) authentication.getPrincipal();

        UserRole requesterRole = UserRole.valueOf(
                authentication.getAuthorities()
                        .iterator()
                        .next()
                        .getAuthority()
                        .replace("ROLE_","")
        );

        return GeneralResponse.toResponseEntity(
                GeneralResponseCode.CREATED,
                createHubInventoryUseCase.create(request.toCommand(requesterId, requesterRole))
        );
    }

    @PatchMapping("/{inventoryId}")
    @PreAuthorize("hasAnyRole('MASTER', 'HUB_MANAGER')")
    public ResponseEntity<GeneralResponse<UpdateHubInventoryResponse>> updateHubInventory(
            @PathVariable UUID inventoryId,
            @Valid @RequestBody UpdateHubInventoryRequest request,
            Authentication authentication
            ){
        UUID requesterId = (UUID) authentication.getPrincipal();

        UserRole requesterRole = UserRole.valueOf(
                authentication.getAuthorities()
                        .iterator()
                        .next()
                        .getAuthority()
                        .replace("ROLE_","")
        );

        UpdateHubInventoryResponse response =
                updateHubInventoryUseCase.update(
                        request.toCommand(
                                inventoryId,
                                requesterId,
                                requesterRole
                        )
                );

        return GeneralResponse.toResponseEntity(
                GeneralResponseCode.OK,
                response
        );
    }

    @PatchMapping("/{inventoryId}/safety-stock")
    @PreAuthorize("hasAnyRole('MASTER', 'HUB_MANAGER')")
    public ResponseEntity<GeneralResponse<UpdateSafetyStockResponse>> updateSafetyStock(
            @PathVariable UUID inventoryId,
            @Valid @RequestBody UpdateSafetyStockRequest request,
            Authentication authentication
            ){
        UUID requesterId = (UUID) authentication.getPrincipal();

        UserRole requesterRole = UserRole.valueOf(
                authentication.getAuthorities()
                        .iterator()
                        .next()
                        .getAuthority()
                        .replace("ROLE_","")
        );

        UpdateSafetyStockResponse response = updateSafetyStockUseCase.updateSafetyStock(request.toCommand(
                inventoryId,
                requesterId,
                requesterRole
                ));

        return GeneralResponse.toResponseEntity(
                GeneralResponseCode.OK,
                response
        );
    }
}
