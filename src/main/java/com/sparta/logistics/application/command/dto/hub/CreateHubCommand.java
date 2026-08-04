package com.sparta.logistics.application.command.dto.hub;

import com.sparta.logistics.presentation.command.request.CreateHubRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class CreateHubCommand {

    private final String name;

    private final String address;

    private final UUID managerId;
}
