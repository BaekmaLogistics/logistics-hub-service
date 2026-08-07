package com.sparta.logistics.presentation.query.controller;

import com.sparta.logistics.application.query.dto.HubInventoryResponse;
import com.sparta.logistics.application.query.dto.HubInventorySearchCondition;
import com.sparta.logistics.application.query.usecase.HubInventoryQueryUseCase;
import com.sparta.logistics.common.code.GeneralResponseCode;
import com.sparta.logistics.presentation.common.dto.response.GeneralResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/hub-inventories")
public class HubInventoryQueryController {

    private final HubInventoryQueryUseCase hubInventoryQueryUseCase;

    @GetMapping("/{inventoryId}")
    public ResponseEntity<GeneralResponse<HubInventoryResponse>> getHubInventory(
            @PathVariable UUID inventoryId
            ) {
        HubInventoryResponse response = hubInventoryQueryUseCase.getHubInventory(inventoryId);

        return GeneralResponse.toResponseEntity(
                GeneralResponseCode.OK,
                response
        );
    }

    @GetMapping
    public ResponseEntity<GeneralResponse<Page<HubInventoryResponse>>> searchHubInventories(
            @ModelAttribute HubInventorySearchCondition condition,
            Pageable pageable
    ){
        Page<HubInventoryResponse> responses = hubInventoryQueryUseCase.searchHubInventories(
                condition,
                pageable
        );

        return GeneralResponse.toResponseEntity(
                GeneralResponseCode.OK,
                responses
        );
    }
}
