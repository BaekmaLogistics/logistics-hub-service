package com.sparta.logistics.infrastructure.feign.client;

import com.sparta.logistics.infrastructure.feign.dto.user.UserResponse;
import com.sparta.logistics.infrastructure.feign.fallback.UserClientFallbackFactory;
import com.sparta.logistics.presentation.common.dto.response.GeneralResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(
        name = "user-service",
        fallbackFactory = UserClientFallbackFactory.class
)
public interface UserClient {

    @GetMapping("/internal/api/v1/users/{userId}")
    GeneralResponse<UserResponse> getUser(
            @PathVariable("userId")UUID userId
            );
}
