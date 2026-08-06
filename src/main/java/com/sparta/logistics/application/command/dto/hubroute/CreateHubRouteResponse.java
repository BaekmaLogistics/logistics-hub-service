package com.sparta.logistics.application.command.dto.hubroute;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Builder
@Getter
@AllArgsConstructor
public class CreateHubRouteResponse {

    private final UUID hubRouteId;

    private final UUID fromHubId;

    private final UUID toHubId;

    private final Double distance;

    private final Integer duration;
}
