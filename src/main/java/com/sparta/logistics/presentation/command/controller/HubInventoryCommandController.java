package com.sparta.logistics.presentation.command.controller;

import com.sparta.logistics.application.command.dto.hubinventory.CreateHubInventoryResponse;
import com.sparta.logistics.application.command.usecase.CreateHubInventoryUseCase;
import com.sparta.logistics.common.code.GeneralResponseCode;
import com.sparta.logistics.presentation.command.request.CreateHubInventoryRequest;
import com.sparta.logistics.presentation.common.dto.response.GeneralResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/hub-inventories")
public class HubInventoryCommandController {

    private final CreateHubInventoryUseCase createHubInventoryUseCase;

    @PostMapping
    public ResponseEntity<GeneralResponse<CreateHubInventoryResponse>> createHubInventory(
            @Valid @RequestBody CreateHubInventoryRequest request
            ) {
        return GeneralResponse.toResponseEntity(
                GeneralResponseCode.CREATED,
                createHubInventoryUseCase.create(request.toCommand())
        );
    }
}
