package com.sparta.logistics.application.graph;

import lombok.Getter;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Getter
public class HubGraph {

    private final Map<UUID, List<Edge>> adjacencyList;

    public HubGraph(Map<UUID, List<Edge>> adjacencyList){
        this.adjacencyList = adjacencyList;
    }

    public List<Edge> getEdges(UUID hubId){
        return adjacencyList.getOrDefault(hubId, Collections.emptyList());
    }
}
