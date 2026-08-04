package com.sparta.logistics.application.command.dto.hub;

import com.sparta.logistics.domain.entity.Hub;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class CreateHubResponse {

    private UUID id;

    private String name;

    private String address;

    private Double latitude;

    private Double longitude;

    private UUID managerId;

    public static CreateHubResponse from(Hub hub){
        return CreateHubResponse.builder()
                .id(hub.getId())
                .name(hub.getName())
                .address(hub.getAddress())
                .latitude(hub.getLatitude())
                .longitude(hub.getLongitude())
                .managerId(hub.getManagerId())
                .build();
    }
}
