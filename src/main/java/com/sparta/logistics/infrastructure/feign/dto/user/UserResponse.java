package com.sparta.logistics.infrastructure.feign.dto.user;

import java.util.UUID;

public record UserResponse(
        UUID userId,
        String role,
        UUID hubId,
        UUID companyId
) {
}
