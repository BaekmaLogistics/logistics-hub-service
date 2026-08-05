package com.sparta.logistics.infrastructure.feign.dto.direction;

import com.sparta.logistics.common.code.ErrorResponseCode;
import com.sparta.logistics.common.exception.ApiException;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class DirectionResponse {

    private Route route;

    public Summary getSummary() {
        return route.getTrafasts()
                .stream()
                .findFirst()
                .orElseThrow(() -> new ApiException(ErrorResponseCode.DIRECTION_NOT_FOUND))
                .getSummary();
    }
}
