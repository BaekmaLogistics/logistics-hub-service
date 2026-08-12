package com.sparta.logistics.application.port;

import com.sparta.logistics.infrastructure.feign.dto.user.UserResponse;

import java.util.UUID;

public interface UserReader {

    UserInfo getUser(UUID userId);

    record UserInfo(
            UUID userId,
            String role,
            UUID hubId,
            UUID companyId
    ){
    }
}
