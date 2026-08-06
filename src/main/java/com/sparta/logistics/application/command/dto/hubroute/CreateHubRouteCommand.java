package com.sparta.logistics.application.command.dto.hubroute;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class CreateHubRouteCommand {

    private final UUID fromHubId;

    private final UUID toHubId;

}
