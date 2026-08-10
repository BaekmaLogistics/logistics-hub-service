package com.sparta.logistics.application.event;

import java.time.Instant;
import java.util.UUID;

public record InventoryLowEvent(
        UUID inventoryId,
        UUID hubId,
        UUID productId,
        int quantity,
        int safetyStock,
        Instant occurredAt
) {
}
