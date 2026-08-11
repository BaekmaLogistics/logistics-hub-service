package com.sparta.logistics.application.port;

import java.util.UUID;

public interface ProductValidator {

    void validateExists(UUID productId);
}
