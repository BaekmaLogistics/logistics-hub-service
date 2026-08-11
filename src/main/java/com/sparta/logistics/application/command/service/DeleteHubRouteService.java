package com.sparta.logistics.application.command.service;

import com.sparta.logistics.application.command.dto.hubroute.DeleteHubRouteCommand;
import com.sparta.logistics.application.command.usecase.DeleteHubRouteUseCase;
import com.sparta.logistics.application.event.HubRouteChangeType;
import com.sparta.logistics.application.event.HubRouteChangedEvent;
import com.sparta.logistics.common.code.ErrorResponseCode;
import com.sparta.logistics.common.exception.ApiException;
import com.sparta.logistics.domain.entity.HubRoute;
import com.sparta.logistics.domain.repository.HubRouteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class DeleteHubRouteService implements DeleteHubRouteUseCase {

    private final HubRouteRepository hubRouteRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public void deleteHubRoute(DeleteHubRouteCommand command){
        HubRoute hubRoute = hubRouteRepository.findById(command.getId())
                .orElseThrow(() -> new ApiException(ErrorResponseCode.HUB_ROUTE_NOT_FOUND));

        if(hubRoute.isDeleted()){
            throw new ApiException(ErrorResponseCode.HUB_ROUTE_ALREADY_DELETED);
        }

        hubRoute.softDelete(command.getDeletedBy());

        //동기화
        eventPublisher.publishEvent(
                new HubRouteChangedEvent(
                        hubRoute.getId(),
                        HubRouteChangeType.DELETED,
                        Instant.now()
                )
        );
    }
}
