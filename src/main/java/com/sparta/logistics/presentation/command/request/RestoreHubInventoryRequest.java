package com.sparta.logistics.presentation.command.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
public class RestoreHubInventoryRequest {

    @NotNull
    private UUID orderId;

    @NotNull
    private UUID hubId;

    @NotNull
    private UUID productId;
}