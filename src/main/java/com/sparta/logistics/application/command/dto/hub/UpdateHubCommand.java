package com.sparta.logistics.application.command.dto.hub;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class UpdateHubCommand {
    private final UUID id;

    private final String name;

    private final String address;

}
