package com.sparta.logistics.application.command.dto.hubroute;

import com.sparta.logistics.domain.entity.HubRoute;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Builder
@Getter
@AllArgsConstructor
public class UpdateHubRouteResponse {

    private final UUID id;

    private final UUID fromHubId;

    private final UUID toHubId;

    private final Double distance;

    private final Integer duration;

    public static UpdateHubRouteResponse from(HubRoute hubRoute){
        return UpdateHubRouteResponse.builder()
                .id(hubRoute.getId())
                .fromHubId(hubRoute.getFromHub().getId())
                .toHubId(hubRoute.getToHub().getId())
                .distance(hubRoute.getDistance())
                .duration(hubRoute.getDuration())
                .build();
    }
}
