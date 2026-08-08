package com.sparta.logistics.presentation.query.controller;

import com.sparta.logistics.application.query.dto.HubDetailResponse;
import com.sparta.logistics.application.query.usecase.HubQueryUseCase;
import com.sparta.logistics.common.code.GeneralResponseCode;
import com.sparta.logistics.presentation.common.dto.response.GeneralResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/api/v1/hubs")
public class InternalHubQueryController {

    private final HubQueryUseCase hubQueryUseCase;

    @GetMapping("/{hubId}")
    public ResponseEntity<GeneralResponse<HubDetailResponse>> getHub(
            @PathVariable UUID hubId
            ) {
        return GeneralResponse.toResponseEntity(
                GeneralResponseCode.OK,
                hubQueryUseCase.getHub(hubId)
        );
    }
}
