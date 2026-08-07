package com.sparta.logistics.presentation.command.request;

import com.sparta.logistics.application.command.dto.hubroute.UpdateHubRouteCommand;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
public class UpdateHubRouteRequest {

    private UUID fromHubId;
    private UUID toHubId;

    public UpdateHubRouteCommand toCommand(UUID hubRouteId){
        return UpdateHubRouteCommand.builder()
                .id(hubRouteId)
                .fromHubId(fromHubId)
                .toHubId(toHubId)
                .build();
    }
}
