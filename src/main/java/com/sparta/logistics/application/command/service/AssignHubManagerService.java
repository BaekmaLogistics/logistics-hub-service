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

    private final UserReader userReader;
    private final HubManagerAssigner hubManagerAssigner;

    @Override
    public AssignHubManagerResponse assign(AssignHubManagerCommand command){

        UserReader.UserInfo user = userReader.getUser(command.getManagerId());

        if(!"HUB_MANAGER".equals(user.role())){
            throw new ApiException(ErrorResponseCode.HUB_MANAGER_ROLE_REQUIRED);
        }

        return hubManagerAssigner.assign(command);
    }
}
