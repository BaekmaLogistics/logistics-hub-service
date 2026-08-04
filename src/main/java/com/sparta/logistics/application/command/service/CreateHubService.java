package com.sparta.logistics.application.command.service;

import com.sparta.logistics.application.command.dto.hub.CreateHubCommand;
import com.sparta.logistics.application.command.dto.hub.CreateHubResponse;
import com.sparta.logistics.application.command.usecase.CreateHubUseCase;
import com.sparta.logistics.application.common.dto.Coordinate;
import com.sparta.logistics.application.common.service.GeocodingService;
import com.sparta.logistics.common.code.ErrorResponseCode;
import com.sparta.logistics.common.exception.ApiException;
import com.sparta.logistics.domain.entity.Hub;
import com.sparta.logistics.domain.repository.HubRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CreateHubService implements CreateHubUseCase {

    private final HubRepository hubRepository;
    private final GeocodingService geocodingService;

    @Override
    @Transactional
    public CreateHubResponse createHub(CreateHubCommand command){
        // TODO: User Service 연동 후
        // managerId 존재 여부 및 HUB_MANAGER 권한 검증
        if(hubRepository.existsByName(command.getName())){
            throw new ApiException(ErrorResponseCode.HUB_ALREADY_EXISTS);
        }

        Coordinate coordinate = geocodingService.getCoordinate(command.getAddress());

        Hub hub = Hub.create(
                command.getName(),
                command.getAddress(),
                coordinate.latitude(),
                coordinate.longitude(),
                command.getManagerId()
        );

        Hub savedHub = hubRepository.save(hub);

        return CreateHubResponse.from(savedHub);
    }
}
