package com.sparta.logistics.presentation.command.controller;

import com.sparta.logistics.application.command.dto.hub.AssignHubManagerResponse;
import com.sparta.logistics.application.command.dto.hub.CreateHubResponse;
import com.sparta.logistics.application.command.dto.hub.DeleteHubCommand;
import com.sparta.logistics.application.command.dto.hub.UpdateHubResponse;
import com.sparta.logistics.application.command.usecase.AssignHubManagerUseCase;
import com.sparta.logistics.application.command.usecase.CreateHubUseCase;
import com.sparta.logistics.application.command.usecase.DeleteHubUseCase;
import com.sparta.logistics.application.command.usecase.UpdateHubUseCase;
import com.sparta.logistics.common.code.GeneralResponseCode;
import com.sparta.logistics.presentation.command.request.AssignHubManagerRequest;
import com.sparta.logistics.presentation.command.request.CreateHubRequest;
import com.sparta.logistics.presentation.command.request.UpdateHubRequest;
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
@RequestMapping("/api/v1/hubs")
public class HubCommandController {

    private final CreateHubUseCase createHubUseCase;
    private final UpdateHubUseCase updateHubUseCase;
    private final DeleteHubUseCase deleteHubUseCase;
    private final AssignHubManagerUseCase assignHubManagerUseCase;

    @PostMapping
    @PreAuthorize("hasRole('MASTER')")
    public ResponseEntity<GeneralResponse<CreateHubResponse>> createHub(
            @Valid @RequestBody CreateHubRequest request
            ) {
        CreateHubResponse response = createHubUseCase.createHub(request.toCommand());

        return GeneralResponse.toResponseEntity(
                GeneralResponseCode.CREATED,
                response
        );
    }

    @PatchMapping("/{hubId}")
    @PreAuthorize("hasRole('MASTER')")
    public ResponseEntity<GeneralResponse<UpdateHubResponse>> updateHub(
            @PathVariable UUID hubId,
            @Valid @RequestBody UpdateHubRequest request
            ) {
        UpdateHubResponse response = updateHubUseCase.updateHub(request.toCommand(hubId));

        return GeneralResponse.toResponseEntity(
                GeneralResponseCode.OK,
                response
        );
    }

    @DeleteMapping("/{hubId}")
    @PreAuthorize("hasRole('MASTER')")
    public ResponseEntity<GeneralResponse<Void>> deleteHub(
            @PathVariable UUID hubId,
            Authentication authentication
    ) {
        UUID userId = (UUID) authentication.getPrincipal();

        DeleteHubCommand command = DeleteHubCommand.builder()
                .id(hubId)
                .deletedBy(userId)
                .build();

        deleteHubUseCase.deleteHub(command);

        return GeneralResponse.toResponseEntity(
                GeneralResponseCode.OK,
                null
        );
    }

    @PatchMapping("/{hubId}/manager")
    @PreAuthorize("hasRole('MASTER')")
    public ResponseEntity<GeneralResponse<AssignHubManagerResponse>> assignManager(
            @PathVariable UUID hubId,
            @Valid @RequestBody AssignHubManagerRequest request
    ) {
        return GeneralResponse.toResponseEntity(
                GeneralResponseCode.OK,
                assignHubManagerUseCase.assign(
                        request.toCommand(hubId)
                )
        );
    }
}
