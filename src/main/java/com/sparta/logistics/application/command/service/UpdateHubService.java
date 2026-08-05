package com.sparta.logistics.application.command.service;

import com.sparta.logistics.application.command.dto.hub.UpdateHubCommand;
import com.sparta.logistics.application.command.dto.hub.UpdateHubResponse;
import com.sparta.logistics.application.command.usecase.UpdateHubUseCase;
import com.sparta.logistics.application.common.dto.Coordinate;
import com.sparta.logistics.application.common.service.GeocodingService;
import com.sparta.logistics.common.code.ErrorResponseCode;
import com.sparta.logistics.common.exception.ApiException;
import com.sparta.logistics.domain.entity.Hub;
import com.sparta.logistics.domain.repository.HubRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UpdateHubService implements UpdateHubUseCase {

    private final HubRepository hubRepository;
    private final GeocodingService geocodingService;

    @Override
    @Transactional
    public UpdateHubResponse updateHub(UpdateHubCommand command){
        Hub hub = hubRepository.findById(command.getId())
                .orElseThrow(() -> new ApiException(ErrorResponseCode.HUB_NOT_FOUND));

         if(hub.isDeleted()){
             throw new ApiException(ErrorResponseCode.HUB_ALREADY_DELETED);
         }

        if(command.getName() != null){
            hub.updateName(command.getName());
        }

        if(!Objects.equals(hub.getAddress(), command.getAddress())){
            Coordinate coordinate = geocodingService.getCoordinate(command.getAddress());

            hub.updateAddress(
                    command.getAddress(),
                    coordinate.latitude(),
                    coordinate.longitude()
            );
        }

        return UpdateHubResponse.from(hub);
    }
}
