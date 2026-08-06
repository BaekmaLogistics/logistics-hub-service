package com.sparta.logistics.domain.graph;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
public class HubNode {

    private final UUID hubId;
    private final double latitude;
    private final double longitude;
    private final List<Edge> edges = new ArrayList<>();

    public HubNode(
            UUID hubId,
            double latitude,
            double longitude
    ) {
        this.hubId = hubId;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public void addEdge(Edge edge) {
        edges.add(edge);
    }
}