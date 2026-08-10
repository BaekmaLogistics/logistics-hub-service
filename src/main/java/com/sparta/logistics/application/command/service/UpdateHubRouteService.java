package com.sparta.logistics.application.command.service;

import com.sparta.logistics.application.command.dto.hubroute.UpdateHubRouteCommand;
import com.sparta.logistics.application.command.dto.hubroute.UpdateHubRouteResponse;
import com.sparta.logistics.application.command.usecase.UpdateHubRouteUseCase;
import com.sparta.logistics.application.common.service.DirectionService;
import com.sparta.logistics.application.event.HubRouteChangeType;
import com.sparta.logistics.application.event.HubRouteChangedEvent;
import com.sparta.logistics.application.event.HubRouteChangedIntegrationEvent;
import com.sparta.logistics.common.code.ErrorResponseCode;
import com.sparta.logistics.common.exception.ApiException;
import com.sparta.logistics.domain.entity.Hub;
import com.sparta.logistics.domain.entity.HubRoute;
import com.sparta.logistics.domain.repository.HubRepository;
import com.sparta.logistics.domain.repository.HubRouteRepository;
import com.sparta.logistics.infrastructure.feign.dto.direction.RouteInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UpdateHubRouteService implements UpdateHubRouteUseCase {

    private final HubRouteRepository hubRouteRepository;
    private final HubRepository hubRepository;
    private final DirectionService directionService;
    private final ApplicationEventPublisher eventPublisher;

    private Hub findHub(UUID hubId){
        Hub hub = hubRepository.findById(hubId)
                .orElseThrow(() -> new ApiException(ErrorResponseCode.HUB_NOT_FOUND));

        if(hub.isDeleted()){
            throw new ApiException(ErrorResponseCode.HUB_ALREADY_DELETED);
        }

        return hub;
    }

    @Override
    @Transactional
    public UpdateHubRouteResponse updateHubRoute(
            UpdateHubRouteCommand command
    ){

        if(command.getFromHubId() == null && command.getToHubId() == null){
            throw new ApiException(ErrorResponseCode.INVALID_HUB_ROUTE_UPDATE);
        }
        //수정 대상 경로 조회
        HubRoute hubRoute = hubRouteRepository.findById(command.getId())
                .orElseThrow(() ->
                        new ApiException(ErrorResponseCode.HUB_ROUTE_NOT_FOUND)
                );

        //삭제된 경로인지
        if(hubRoute.isDeleted()){
            throw new ApiException(ErrorResponseCode.HUB_ROUTE_ALREADY_DELETED);
        }

        //null이면 기존 내용은 유지
        Hub fromHub = command.getFromHubId() != null ? findHub(command.getFromHubId()) : hubRoute.getFromHub();
        Hub toHub = command.getToHubId() != null ? findHub(command.getToHubId()) : hubRoute.getToHub();

        //같은 허브끼리는 연결 방지
        if(fromHub.getId().equals(toHub.getId())){
            throw new ApiException(ErrorResponseCode.INVALID_HUB_ROUTE);
        }

        //자기 자신을 제외한 활성 경로 중복 검사
        if(hubRouteRepository.existsByFromHubAndToHubAndIdNotAndDeletedAtIsNull(
                fromHub,
                toHub,
                hubRoute.getId()
        )){
            throw new ApiException(
                    ErrorResponseCode.HUB_ROUTE_ALREADY_EXISTS
            );
        }

        //실제 좌표 기준 거리/시간 재계산
        RouteInfo routeInfo = directionService.getRoute(fromHub, toHub);

        //엔티티 변경
        hubRoute.update(
                fromHub,
                toHub,
                routeInfo.getDistance(),
                routeInfo.getDuration()
        );

        //현재 인스턴스 갱신을 위한 이벤트
        eventPublisher.publishEvent(
                new HubRouteChangedEvent()
        );

        //다른 인스턴스 동기화
        eventPublisher.publishEvent(
                new HubRouteChangedIntegrationEvent(
                        hubRoute.getId(),
                        HubRouteChangeType.UPDATED,
                        Instant.now()
                )
        );

        return UpdateHubRouteResponse.from(hubRoute);
    }
}
