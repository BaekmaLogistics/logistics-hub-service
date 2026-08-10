package com.sparta.logistics.application.common.validator;

import com.sparta.logistics.common.code.ErrorResponseCode;
import com.sparta.logistics.common.exception.ApiException;
import com.sparta.logistics.domain.entity.Hub;
import com.sparta.logistics.domain.model.UserRole;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.UUID;

@Component
public class HubAccessValidator {

    public void validate(
            Hub hub,
            UUID userId,
            UserRole role
    ) {
        if(role == UserRole.MASTER){
            return;
        }

        if(role == UserRole.HUB_MANAGER && Objects.equals(hub.getManagerId(), userId)){
            return;
        }

        throw new ApiException(ErrorResponseCode.HUB_ACCESS_DENIED);
    }
}
