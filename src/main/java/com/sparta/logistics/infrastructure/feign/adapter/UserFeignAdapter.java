package com.sparta.logistics.infrastructure.feign.adapter;

import com.sparta.logistics.application.port.UserReader;
import com.sparta.logistics.common.code.ErrorResponseCode;
import com.sparta.logistics.common.exception.ApiException;
import com.sparta.logistics.infrastructure.feign.client.UserClient;
import com.sparta.logistics.infrastructure.feign.dto.user.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UserFeignAdapter implements UserReader {

    private final UserClient userClient;

    @Override
    public UserInfo getUser(UUID userId){
        UserResponse response = userClient.getUser(userId).data();

        if(!userId.equals(response.userId())){
            throw new ApiException(ErrorResponseCode.EXTERNAL_SERVICE_INVALID_RESPONSE);
        }

        return new UserInfo(
                response.userId(),
                response.role(),
                response.hubId(),
                response.companyId()
        );
    }
}
