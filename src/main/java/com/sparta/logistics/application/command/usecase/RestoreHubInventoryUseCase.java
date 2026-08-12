package com.sparta.logistics.application.command.usecase;

import java.util.UUID;

public interface RestoreHubInventoryUseCase {

    void restore(
            UUID orderId,
            UUID hubId,
            UUID productId
    );
}
