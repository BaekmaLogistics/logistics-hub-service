package com.sparta.logistics.application.command.dto.hub;

import com.sparta.logistics.domain.entity.Hub;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Builder
@Getter
@AllArgsConstructor
public class AssignHubManagerResponse {

    private final UUID hubId;

    private final UUID managerId;

    public static AssignHubManagerResponse from(Hub hub){
        return AssignHubManagerResponse.builder()
                .hubId(hub.getId())
                .managerId(hub.getManagerId())
                .build();
    }
}
