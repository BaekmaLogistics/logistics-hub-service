package com.sparta.logistics.application.command.service;

import com.sparta.logistics.application.command.dto.hub.DeleteHubCommand;
import com.sparta.logistics.application.command.usecase.DeleteHubRoutesUseCase;
import com.sparta.logistics.application.command.usecase.DeleteHubUseCase;
import com.sparta.logistics.application.event.HubDeletedEvent;
import com.sparta.logistics.common.code.ErrorResponseCode;
import com.sparta.logistics.common.exception.ApiException;
import com.sparta.logistics.domain.entity.Hub;
import com.sparta.logistics.domain.repository.HubRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DeleteHubService implements DeleteHubUseCase {

    private final HubRepository hubRepository;
    private final DeleteHubRoutesUseCase deleteHubRoutesUseCase;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public void deleteHub(DeleteHubCommand command){
        Hub hub = hubRepository.findById(command.getId())
                .orElseThrow(() -> new ApiException(ErrorResponseCode.HUB_NOT_FOUND));

        if(hub.isDeleted()){
            throw new ApiException(ErrorResponseCode.HUB_ALREADY_DELETED);
        }

        deleteHubRoutesUseCase.deleteRoutesByHub(
                hub,
                command.getDeletedBy()
        );

        hub.softDelete(command.getDeletedBy());

        eventPublisher.publishEvent(
                new HubDeletedEvent(
                        hub.getId(),
                        hub.getDeletedAt()
                )
        );
    }
}
