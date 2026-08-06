package com.sparta.logistics.application.graph;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class Edge {

    private final UUID toHubId;

    private final double distance;

    private final double duration;
}
