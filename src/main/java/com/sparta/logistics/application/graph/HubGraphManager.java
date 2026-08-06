package com.sparta.logistics.application.graph;

import com.sparta.logistics.domain.entity.HubRoute;
import com.sparta.logistics.domain.repository.HubRouteRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class HubGraphManager {

    private final HubRouteRepository hubRouteRepository;

    private volatile HubGraph hubGraph;

    @PostConstruct
    public void initialize(){
        reloadGraph();
    }

    public HubGraph getGraph() {
        return hubGraph;
    }

    public void reloadGraph(){
        List<HubRoute> routes = hubRouteRepository.findAllByDeletedAtIsNull();
        Map<UUID, List<Edge>> adjacencyList = new HashMap<>();

        for(HubRoute route : routes){
            adjacencyList.computeIfAbsent(route.getFromHub().getId(),
                    id -> new ArrayList<>())
                    .add(
                            new Edge(
                                    route.getToHub().getId(),
                                    route.getDistance(),
                                    route.getDuration()
                            )
                    );
        }

        hubGraph = new HubGraph(adjacencyList);
    }
}
