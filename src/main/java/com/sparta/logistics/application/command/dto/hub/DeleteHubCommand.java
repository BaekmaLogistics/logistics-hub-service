package com.sparta.logistics.application.command.dto.hub;

import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Builder
@Getter
public class DeleteHubCommand {
    private UUID id;

    private UUID deletedBy;
}
