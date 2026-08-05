package com.sparta.logistics.presentation.command.request;

import com.sparta.logistics.application.command.dto.hub.UpdateHubCommand;
import lombok.*;

import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UpdateHubRequest {
    private String name;

    private String address;

    private UUID managerId;

    public UpdateHubCommand toCommand(UUID hubId){
        return UpdateHubCommand.builder()
                .id(hubId)
                .name(name)
                .address(address)
                .managerId(managerId)
                .build();

    }

}
