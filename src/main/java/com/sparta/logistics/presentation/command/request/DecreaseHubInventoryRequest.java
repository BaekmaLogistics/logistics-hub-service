package com.sparta.logistics.presentation.command.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;

import java.util.UUID;

@Getter
public class DecreaseHubInventoryRequest {

    @NotNull
    private UUID orderId;

    @NotNull
    private UUID hubId;

    @NotNull
    private UUID productId;

    @NotNull
    @Positive
    private Integer quantity;
}
