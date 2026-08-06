package com.sparta.logistics.application.query.dto;

import com.sparta.logistics.domain.entity.HubRoute;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class HubRouteDetailResponse {

    private UUID hubRouteId;

    private UUID fromHubId;
    private String fromHubName;

    private UUID toHubId;
    private String toHubName;

    private Double distance;

    private Integer duration;

    public static HubRouteDetailResponse from(HubRoute hubRoute) {
        return new HubRouteDetailResponse(
                hubRoute.getId(),
                hubRoute.getFromHub().getId(),
                hubRoute.getFromHub().getName(),
                hubRoute.getToHub().getId(),
                hubRoute.getToHub().getName(),
                hubRoute.getDistance(),
                hubRoute.getDuration()
        );
    }
}
