package com.sparta.logistics.presentation.command.controller;

import com.sparta.logistics.application.command.dto.hub.CreateHubResponse;
import com.sparta.logistics.application.command.usecase.CreateHubUseCase;
import com.sparta.logistics.common.code.GeneralResponseCode;
import com.sparta.logistics.presentation.command.request.CreateHubRequest;
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
@RequestMapping("/api/v1")
public class HubCommandController {

    private final CreateHubUseCase createHubUseCase;

    @PostMapping("/hubs")
    public ResponseEntity<GeneralResponse<CreateHubResponse>> createHub(
            @Valid @RequestBody CreateHubRequest request
            ) {
        CreateHubResponse response = createHubUseCase.createHub(request.toCommand());

        return GeneralResponse.toResponseEntity(
                GeneralResponseCode.CREATED,
                response
        );
    }
}
