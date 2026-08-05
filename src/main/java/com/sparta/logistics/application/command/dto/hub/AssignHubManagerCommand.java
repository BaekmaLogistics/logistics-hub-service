package com.sparta.logistics.application.command.dto.hub;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Builder
@Getter
@AllArgsConstructor
public class AssignHubManagerCommand {

    private final UUID hubId;

    private final UUID managerId;
}
