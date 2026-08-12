package com.sparta.logistics.infrastructure.feign.fallback;

import com.sparta.logistics.common.code.ErrorResponseCode;
import com.sparta.logistics.common.exception.ApiException;
import com.sparta.logistics.infrastructure.feign.client.UserClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class UserClientFallbackFactory implements FallbackFactory<UserClient> {

    @Override
    public UserClient create(Throwable cause){
        return userId -> {
            log.error(
                    "User Service 호출 실패. userId={}",
                    userId,
                    cause
            );

            throw new ApiException(ErrorResponseCode.EXTERNAL_SERVICE_UNAVAILABLE);
        };
    }
}
