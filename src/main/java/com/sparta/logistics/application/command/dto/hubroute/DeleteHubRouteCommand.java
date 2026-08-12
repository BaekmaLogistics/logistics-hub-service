package com.sparta.logistics.application.command.dto.hubroute;

import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class DeleteHubRouteCommand {

    private UUID id;
    private UUID deletedBy;
}
