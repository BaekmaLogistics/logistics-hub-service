package com.sparta.logistics.presentation.query.controller;

import com.sparta.logistics.application.query.dto.HubInventoryResponse;
import com.sparta.logistics.application.query.dto.HubInventorySearchCondition;
import com.sparta.logistics.application.query.usecase.HubInventoryQueryUseCase;
import com.sparta.logistics.common.code.GeneralResponseCode;
import com.sparta.logistics.domain.model.UserRole;
import com.sparta.logistics.presentation.common.dto.response.GeneralResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/hub-inventories")
public class HubInventoryQueryController {

    private final HubInventoryQueryUseCase hubInventoryQueryUseCase;

    @GetMapping("/{inventoryId}")
    @PreAuthorize("hasAnyRole('MASTER', 'HUB_MANAGER')")
    public ResponseEntity<GeneralResponse<HubInventoryResponse>> getHubInventory(
            @PathVariable UUID inventoryId,
            Authentication authentication
            ) {
        UUID requesterId = (UUID) authentication.getPrincipal();

        UserRole requesterRole = UserRole.valueOf(
                authentication.getAuthorities()
                        .iterator()
                        .next()
                        .getAuthority()
                        .replace("ROLE_", "")
        );

        HubInventoryResponse response = hubInventoryQueryUseCase.getHubInventory(
                inventoryId,
                requesterId,
                requesterRole
                );

        return GeneralResponse.toResponseEntity(
                GeneralResponseCode.OK,
                response
        );
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('MASTER', 'HUB_MANAGER')")
    public ResponseEntity<GeneralResponse<Page<HubInventoryResponse>>> searchHubInventories(
            @ModelAttribute HubInventorySearchCondition condition,
            Pageable pageable,
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

        Page<HubInventoryResponse> responses = hubInventoryQueryUseCase.searchHubInventories(
                condition,
                pageable,
                requesterId,
                requesterRole
        );

        return GeneralResponse.toResponseEntity(
                GeneralResponseCode.OK,
                responses
        );
    }
}
