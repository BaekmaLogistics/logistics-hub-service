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
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class HubCommandController {

    private final CreateHubUseCase createHubUseCase;
    private final UpdateHubUseCase updateHubUseCase;
    private final DeleteHubUseCase deleteHubUseCase;
    private final AssignHubManagerUseCase assignHubManagerUseCase;

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

    @PatchMapping("/hubs/{hubId}")
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

    @DeleteMapping("/hubs/{hubId}")
    public ResponseEntity<GeneralResponse<Void>> deleteHub(
            @PathVariable UUID hubId
    ) {
        DeleteHubCommand command = DeleteHubCommand.builder()
                .id(hubId)
                .deletedBy(UUID.randomUUID()) // TODO: JWT 연동 후 로그인 사용자 ID로 변경
                .build();

        deleteHubUseCase.deleteHub(command);

        return GeneralResponse.toResponseEntity(
                GeneralResponseCode.OK,
                null
        );
    }

    @PatchMapping("/hubs/{hubId}/manager")
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
