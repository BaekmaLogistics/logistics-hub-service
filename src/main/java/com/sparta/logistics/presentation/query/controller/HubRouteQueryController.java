package com.sparta.logistics.presentation.query.controller;

import com.sparta.logistics.application.query.dto.HubRouteDetailResponse;
import com.sparta.logistics.application.query.dto.HubRouteSearchCondition;
import com.sparta.logistics.application.query.usecase.HubRouteQueryUseCase;
import com.sparta.logistics.common.code.GeneralResponseCode;
import com.sparta.logistics.presentation.common.dto.response.GeneralResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/hub-routes")
public class HubRouteQueryController {

    private final HubRouteQueryUseCase hubRouteQueryUseCase;

    @GetMapping
    public ResponseEntity<GeneralResponse<Page<HubRouteDetailResponse>>> searchHubRoutes(
            @ModelAttribute HubRouteSearchCondition condition,
            @PageableDefault(
                    page = 0,
                    size = 10,
                    sort = "createdAt"
            ) Pageable pageable
            ){
        return GeneralResponse.toResponseEntity(
                GeneralResponseCode.OK,
                hubRouteQueryUseCase.getHubRoutes(condition, pageable)
        );

    }
}
