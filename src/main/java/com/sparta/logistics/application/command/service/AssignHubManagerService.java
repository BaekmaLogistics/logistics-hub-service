package com.sparta.logistics.application.command.service;

import com.sparta.logistics.application.command.dto.hub.AssignHubManagerCommand;
import com.sparta.logistics.application.command.dto.hub.AssignHubManagerResponse;
import com.sparta.logistics.application.command.usecase.AssignHubManagerUseCase;
import com.sparta.logistics.application.port.UserReader;
import com.sparta.logistics.common.code.ErrorResponseCode;
import com.sparta.logistics.common.exception.ApiException;
import com.sparta.logistics.domain.entity.Hub;
import com.sparta.logistics.domain.repository.HubRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AssignHubManagerService implements AssignHubManagerUseCase {

    private final HubRepository hubRepository;
    private final UserReader userReader;

    @Override
    @Transactional
    public AssignHubManagerResponse assign(AssignHubManagerCommand command){

        Hub hub = hubRepository.findById(command.getHubId())
                .orElseThrow(() -> new ApiException(ErrorResponseCode.HUB_NOT_FOUND));

        if(hub.isDeleted()){
            throw new ApiException(ErrorResponseCode.HUB_ALREADY_DELETED);
        }

        if(Objects.equals(hub.getManagerId(), command.getManagerId())){
            throw new ApiException(ErrorResponseCode.HUB_MANAGER_ALREADY_ASSIGNED);
        }

        if(hubRepository.existsByManagerIdAndDeletedAtIsNull(command.getManagerId())){
            throw new ApiException(ErrorResponseCode.HUB_MANAGER_ALREADY_ASSIGNED);
        }

        UserReader.UserInfo user = userReader.getUser(command.getManagerId());

        if(!"HUB_MANAGER".equals(user.role())){
            throw new ApiException(ErrorResponseCode.HUB_MANAGER_ROLE_REQUIRED);
        }

        hub.assignManagerId(command.getManagerId());

        return AssignHubManagerResponse.from(hub);
    }
}
