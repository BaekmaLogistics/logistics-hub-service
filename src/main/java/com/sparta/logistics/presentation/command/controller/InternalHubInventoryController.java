package com.sparta.logistics.presentation.command.controller;

import com.sparta.logistics.application.command.usecase.DecreaseHubInventoryUseCase;
import com.sparta.logistics.common.code.GeneralResponseCode;
import com.sparta.logistics.presentation.command.request.DecreaseHubInventoryRequest;
import com.sparta.logistics.presentation.common.dto.response.GeneralResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/api/v1/hub-inventories")
public class InternalHubInventoryController {

    private final DecreaseHubInventoryUseCase decreaseHubInventoryUseCase;

    @PatchMapping("/decrease")
    public ResponseEntity<GeneralResponse<Void>> decrease(
            @Valid @RequestBody DecreaseHubInventoryRequest request
            ){
        decreaseHubInventoryUseCase.decrease(
                request.getOrderId(),
                request.getHubId(),
                request.getProductId(),
                request.getQuantity()
        );

        return GeneralResponse.toResponseEntity(
                GeneralResponseCode.OK,
                null
        );
    }
}
