package com.sparta.logistics.application.command.service;

import com.sparta.logistics.application.command.usecase.RefreshHubRouteUseCase;
import com.sparta.logistics.application.common.service.DirectionService;
import com.sparta.logistics.application.event.HubRouteChangedEvent;
import com.sparta.logistics.domain.entity.Hub;
import com.sparta.logistics.domain.entity.HubRoute;
import com.sparta.logistics.domain.repository.HubRouteRepository;
import com.sparta.logistics.infrastructure.feign.dto.direction.RouteInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RefreshHubRouteService implements RefreshHubRouteUseCase {

    private final HubRouteRepository hubRouteRepository;
    private final DirectionService directionService;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void refreshRoutesByHub(Hub hub){
        List<HubRoute> routes = hubRouteRepository.findAllActiveRoutesByHub(hub);

        for(HubRoute route : routes){
            RouteInfo routeInfo = directionService.getRoute(
                    route.getFromHub(),
                    route.getToHub()
            );

            route.updateRouteInfo(
                    routeInfo.getDistance(),
                    routeInfo.getDuration()
            );
        }

        if(!routes.isEmpty()){
            applicationEventPublisher.publishEvent(
                    new HubRouteChangedEvent()
            );
        }
    }
}
