package com.sparta.logistics.application.command.service;

import com.sparta.logistics.application.command.dto.hub.AssignHubManagerCommand;
import com.sparta.logistics.application.command.dto.hub.AssignHubManagerResponse;
import com.sparta.logistics.common.code.ErrorResponseCode;
import com.sparta.logistics.common.exception.ApiException;
import com.sparta.logistics.domain.entity.Hub;
import com.sparta.logistics.domain.repository.HubRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class HubManagerAssigner {

    private final HubRepository hubRepository;

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

        hub.assignManagerId(command.getManagerId());

        return AssignHubManagerResponse.from(hub);
    }
}
