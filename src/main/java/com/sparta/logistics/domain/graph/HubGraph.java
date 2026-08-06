package com.sparta.logistics.domain.graph;

import lombok.Getter;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;

@Getter
public class HubGraph {

    private final Map<UUID, HubNode> nodes;

    public HubGraph(Map<UUID, HubNode> nodes) {
        this.nodes = Map.copyOf(nodes);
    }

    public HubNode getNode(UUID hubId) {
        return nodes.get(hubId);
    }

    public Collection<HubNode> getNodes() {
        return nodes.values();
    }
}