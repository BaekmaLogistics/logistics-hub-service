package com.sparta.logistics.presentation.query.controller;

import com.sparta.logistics.application.query.dto.HubDetailResponse;
import com.sparta.logistics.application.query.dto.HubSearchCondition;
import com.sparta.logistics.application.query.usecase.HubQueryUseCase;
import com.sparta.logistics.common.code.GeneralResponseCode;
import com.sparta.logistics.presentation.common.dto.response.GeneralResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/hubs")
public class HubQueryController {

    private final HubQueryUseCase hubQueryUseCase;

    @GetMapping("/{hubId}")
    @PreAuthorize("hasAnyRole('MASTER', 'HUB_MANAGER', 'DELIVERY_MANAGER', 'SUPPLIER_MANAGER')")
    public ResponseEntity<GeneralResponse<HubDetailResponse>> getHub(
            @PathVariable UUID hubId
            ) {
        return GeneralResponse.toResponseEntity(
                GeneralResponseCode.OK,
                hubQueryUseCase.getHub(hubId)
        );
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('MASTER', 'HUB_MANAGER', 'DELIVERY_MANAGER', 'SUPPLIER_MANAGER')")
    public ResponseEntity<GeneralResponse<Page<HubDetailResponse>>> searchHubs(
        @ModelAttribute HubSearchCondition condition,
        @PageableDefault(
                page = 0,
                size = 10,
                sort = "createdAt"
        ) Pageable pageable
    ) {

        return GeneralResponse.toResponseEntity(
                GeneralResponseCode.OK,
                hubQueryUseCase.searchHubs(condition, pageable)
        );
    }
}
