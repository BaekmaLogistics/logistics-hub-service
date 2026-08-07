package com.sparta.logistics.application.query.dto;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HubInventorySearchCondition {

    private UUID hubId;
    private UUID productId;
}
