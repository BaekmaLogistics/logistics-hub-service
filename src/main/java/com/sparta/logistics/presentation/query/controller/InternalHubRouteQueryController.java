package com.sparta.logistics.presentation.query.controller;

import com.sparta.logistics.application.query.dto.ShortestPathResponse;
import com.sparta.logistics.application.query.usecase.FindShortestPathUseCase;
import com.sparta.logistics.common.code.GeneralResponseCode;
import com.sparta.logistics.presentation.common.dto.response.GeneralResponse;
import com.sparta.logistics.presentation.query.request.FindShortestPathRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/api/v1/hub-routes")
public class InternalHubRouteQueryController {

    private final FindShortestPathUseCase findShortestPathUseCase;

    @GetMapping("/shortest")
    public ResponseEntity<GeneralResponse<ShortestPathResponse>> findShortestPath(
            @Valid @ModelAttribute FindShortestPathRequest request
            ) {
        return GeneralResponse.toResponseEntity(
                GeneralResponseCode.OK,
                findShortestPathUseCase.findShortestPath(request.getFromHubId(), request.getToHubId())
        );
    }
}
