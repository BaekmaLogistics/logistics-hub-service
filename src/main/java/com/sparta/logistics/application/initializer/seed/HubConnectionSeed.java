package com.sparta.logistics.application.initializer.seed;

public record HubConnectionSeed(
        HubSeed fromHub,
        HubSeed toHub
) {
}
