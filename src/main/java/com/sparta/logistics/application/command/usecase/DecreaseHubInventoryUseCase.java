package com.sparta.logistics.application.command.usecase;

import java.util.UUID;

public interface DecreaseHubInventoryUseCase {

    void decrease(
            UUID hubId,
            UUID productId,
            int quantity
    );
}
