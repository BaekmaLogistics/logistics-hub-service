package com.sparta.logistics.presentation.command.request;

import com.sparta.logistics.application.command.dto.hubroute.CreateHubRouteCommand;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateHubRouteRequest {

    @NotNull
    private UUID fromHubId;

    @NotNull
    private UUID toHubId;

    public CreateHubRouteCommand toCommand() {
        return CreateHubRouteCommand.builder()
                .fromHubId(fromHubId)
                .toHubId(toHubId)
                .build();
    }
}
