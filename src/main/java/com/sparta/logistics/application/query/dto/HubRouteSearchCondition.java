package com.sparta.logistics.application.query.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HubRouteSearchCondition {

    private UUID fromHubId;
    private UUID toHubId;
    private String keyword;
}
