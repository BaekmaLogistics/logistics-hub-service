package com.sparta.logistics.presentation.command.controller;

import com.sparta.logistics.application.command.dto.hubroute.CreateHubRouteResponse;
import com.sparta.logistics.application.command.usecase.CreateHubRouteUseCase;
import com.sparta.logistics.common.code.GeneralResponseCode;
import com.sparta.logistics.presentation.command.request.CreateHubRouteRequest;
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
@RequestMapping("/api/v1/hub-routes")
public class HubRouteCommandController {

    private final CreateHubRouteUseCase createHubRouteUseCase;

    @PostMapping
    public ResponseEntity<GeneralResponse<CreateHubRouteResponse>> createHubRoute(
            @Valid @RequestBody CreateHubRouteRequest request
            ){
        return GeneralResponse.toResponseEntity(
                GeneralResponseCode.CREATED,
                createHubRouteUseCase.create(request.toCommand())
        );
    }
}
