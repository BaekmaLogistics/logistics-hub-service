package com.sparta.logistics.application.command.dto.hub;

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

    public static CreateHubCommand from(CreateHubRequest request){
        return CreateHubCommand.builder()
                .name(request.getName())
                .address(request.getAddress())
                .managerId(request.getManagerId())
                .build();
    }
}
