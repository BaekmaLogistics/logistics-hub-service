package com.sparta.logistics.application.command.service;

import com.sparta.logistics.application.command.dto.hubroute.CreateHubRouteCommand;
import com.sparta.logistics.application.command.dto.hubroute.CreateHubRouteResponse;
import com.sparta.logistics.application.command.usecase.CreateHubRouteUseCase;
import com.sparta.logistics.application.common.service.DirectionService;
import com.sparta.logistics.common.code.ErrorResponseCode;
import com.sparta.logistics.common.exception.ApiException;
import com.sparta.logistics.domain.entity.Hub;
import com.sparta.logistics.domain.entity.HubRoute;
import com.sparta.logistics.domain.repository.HubRepository;
import com.sparta.logistics.domain.repository.HubRouteRepository;
import com.sparta.logistics.infrastructure.feign.dto.direction.RouteInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CreateHubRouteService implements CreateHubRouteUseCase {

    private final HubRepository hubRepository;
    private final HubRouteRepository hubRouteRepository;
    private final DirectionService directionService;

    private void validate(CreateHubRouteCommand command){
        if(command.getFromHubId().equals(command.getToHubId())){
            throw new ApiException(ErrorResponseCode.INVALID_HUB_ROUTE);
        }

        if(hubRouteRepository.existsByFromHubIdAndToHubId(command.getFromHubId(), command.getToHubId())){
            throw new ApiException(ErrorResponseCode.HUB_ROUTE_ALREADY_EXISTS);
        }
    }

    private Hub findHub(UUID hubId){
        return hubRepository.findById(hubId)
                .orElseThrow(() -> new ApiException(ErrorResponseCode.HUB_NOT_FOUND));
    }

    @Transactional
    @Override
    public CreateHubRouteResponse create(CreateHubRouteCommand command){

        validate(command);

        Hub fromHub = findHub(command.getFromHubId());
        Hub toHub = findHub(command.getToHubId());

        RouteInfo routeInfo = directionService.getRoute(fromHub, toHub);

        HubRoute hubRoute = HubRoute.builder()
                .fromHubId(fromHub.getId())
                .toHubId(toHub.getId())
                .distance(routeInfo.getDistance())
                .duration(routeInfo.getDuration())
                .build();
        try{
            HubRoute savedHubRouted = hubRouteRepository.saveAndFlush(hubRoute);

            return CreateHubRouteResponse.builder()
                    .hubRouteId(savedHubRouted.getId())
                    .fromHubId(savedHubRouted.getFromHubId())
                    .toHubId(savedHubRouted.getToHubId())
                    .distance(savedHubRouted.getDistance())
                    .duration(savedHubRouted.getDuration())
                    .build();
        } catch (DataIntegrityViolationException e) {
            throw new ApiException(ErrorResponseCode.HUB_ROUTE_ALREADY_EXISTS);
        }
    }
}
