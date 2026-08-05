package com.sparta.logistics.application.query.dto;

import com.sparta.logistics.domain.entity.Hub;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class HubDetailResponse {

    private UUID id;
    private String name;
    private String address;
    private Double latitude;
    private Double longitude;
    private UUID managerId;

    public static HubDetailResponse from(Hub hub){
        return new HubDetailResponse(
                hub.getId(),
                hub.getName(),
                hub.getAddress(),
                hub.getLatitude(),
                hub.getLongitude(),
                hub.getManagerId()
        );
    }
}
