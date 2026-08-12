package com.sparta.logistics.domain.graph;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class Edge {

    private final UUID toHubId;

    private final double distance;

    private final int duration;
}
