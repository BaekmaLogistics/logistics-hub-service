package com.sparta.logistics.application.command.service;

import com.sparta.logistics.application.command.dto.hubroute.CreateHubRouteCommand;
import com.sparta.logistics.application.command.dto.hubroute.CreateHubRouteResponse;
import com.sparta.logistics.application.command.usecase.CreateHubRouteUseCase;
import com.sparta.logistics.application.common.service.DirectionService;
import com.sparta.logistics.application.event.HubRouteChangedEvent;
import com.sparta.logistics.application.graph.HubGraphManager;
import com.sparta.logistics.common.code.ErrorResponseCode;
import com.sparta.logistics.common.exception.ApiException;
import com.sparta.logistics.domain.entity.Hub;
import com.sparta.logistics.domain.entity.HubRoute;
import com.sparta.logistics.domain.repository.HubRepository;
import com.sparta.logistics.domain.repository.HubRouteRepository;
import com.sparta.logistics.infrastructure.feign.dto.direction.RouteInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
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
    private final ApplicationEventPublisher eventPublisher;

    private void validate(Hub fromHub, Hub toHub) {

        if (hubRouteRepository.existsByFromHubAndToHub(fromHub, toHub)) {
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

        if (command.getFromHubId().equals(command.getToHubId())) {
            throw new ApiException(ErrorResponseCode.INVALID_HUB_ROUTE);
        }

        Hub fromHub = findHub(command.getFromHubId());
        Hub toHub = findHub(command.getToHubId());

        validate(fromHub, toHub);

        RouteInfo routeInfo = directionService.getRoute(fromHub, toHub);

        HubRoute hubRoute = HubRoute.builder()
                .fromHub(fromHub)
                .toHub(toHub)
                .distance(routeInfo.getDistance())
                .duration(routeInfo.getDuration())
                .build();
        try{
            HubRoute savedHubRoute = hubRouteRepository.saveAndFlush(hubRoute);

            eventPublisher.publishEvent(new HubRouteChangedEvent());

            return CreateHubRouteResponse.builder()
                    .hubRouteId(savedHubRoute.getId())
                    .fromHubId(savedHubRoute.getFromHub().getId())
                    .toHubId(savedHubRoute.getToHub().getId())
                    .distance(savedHubRoute.getDistance())
                    .duration(savedHubRoute.getDuration())
                    .build();
        } catch (DataIntegrityViolationException e) {
            throw new ApiException(ErrorResponseCode.HUB_ROUTE_ALREADY_EXISTS);
        }
    }
}
