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
        if (route == null ||
                route.getTrafasts() == null ||
                route.getTrafasts().isEmpty()) {
            throw new ApiException(ErrorResponseCode.DIRECTION_NOT_FOUND);
        }

        Trafast trafast = route.getTrafasts().get(0);

        if (trafast == null || trafast.getSummary() == null) {
            throw new ApiException(ErrorResponseCode.DIRECTION_NOT_FOUND);
        }

        return trafast.getSummary();
    }
}
