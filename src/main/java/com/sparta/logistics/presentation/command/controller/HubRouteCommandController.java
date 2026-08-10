package com.sparta.logistics.presentation.command.controller;

import com.sparta.logistics.application.command.dto.hubroute.CreateHubRouteResponse;
import com.sparta.logistics.application.command.dto.hubroute.DeleteHubRouteCommand;
import com.sparta.logistics.application.command.dto.hubroute.UpdateHubRouteResponse;
import com.sparta.logistics.application.command.usecase.CreateHubRouteUseCase;
import com.sparta.logistics.application.command.usecase.DeleteHubRouteUseCase;
import com.sparta.logistics.application.command.usecase.UpdateHubRouteUseCase;
import com.sparta.logistics.common.code.GeneralResponseCode;
import com.sparta.logistics.presentation.command.request.CreateHubRouteRequest;
import com.sparta.logistics.presentation.command.request.UpdateHubRouteRequest;
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
@RequestMapping("/api/v1/hub-routes")
public class HubRouteCommandController {

    private final CreateHubRouteUseCase createHubRouteUseCase;
    private final UpdateHubRouteUseCase updateHubRouteUseCase;
    private final DeleteHubRouteUseCase deleteHubRouteUseCase;

    @PostMapping
    @PreAuthorize("hasRole('MASTER')")
    public ResponseEntity<GeneralResponse<CreateHubRouteResponse>> createHubRoute(
            @Valid @RequestBody CreateHubRouteRequest request
            ){
        return GeneralResponse.toResponseEntity(
                GeneralResponseCode.CREATED,
                createHubRouteUseCase.create(request.toCommand())
        );
    }

    @PatchMapping("/{hubRouteId}")
    @PreAuthorize("hasRole('MASTER')")
    public ResponseEntity<GeneralResponse<UpdateHubRouteResponse>> updateHubRoute(
            @PathVariable UUID hubRouteId,
            @Valid @RequestBody UpdateHubRouteRequest request
            ){

        UpdateHubRouteResponse response = updateHubRouteUseCase.updateHubRoute(
                request.toCommand(hubRouteId)
        );

        return GeneralResponse.toResponseEntity(
                GeneralResponseCode.OK,
                response
        );
    }

    @DeleteMapping("/{hubRouteId}")
    @PreAuthorize("hasRole('MASTER')")
    public ResponseEntity<GeneralResponse<Void>> deleteHubRoute(
            @PathVariable UUID hubRouteId,
            Authentication authentication
    ) {
        UUID userId = (UUID) authentication.getPrincipal();

        DeleteHubRouteCommand command = DeleteHubRouteCommand.builder()
                .id(hubRouteId)
                .deletedBy(userId)
                .build();

        deleteHubRouteUseCase.deleteHubRoute(command);

        return GeneralResponse.toResponseEntity(
                GeneralResponseCode.OK,
                null
        );
    }
}
