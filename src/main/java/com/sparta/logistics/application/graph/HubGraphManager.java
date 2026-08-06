package com.sparta.logistics.application.graph;

import com.sparta.logistics.domain.entity.Hub;
import com.sparta.logistics.domain.entity.HubRoute;
import com.sparta.logistics.domain.graph.Edge;
import com.sparta.logistics.domain.graph.HubGraph;
import com.sparta.logistics.domain.graph.HubNode;
import com.sparta.logistics.domain.repository.HubRepository;
import com.sparta.logistics.domain.repository.HubRouteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class HubGraphManager {

    private final HubRouteRepository hubRouteRepository;
    private final HubRepository hubRepository;

    private volatile HubGraph hubGraph;

    public HubGraph getGraph() {
        return hubGraph;
    }

    public void reloadGraph(){
        List<Hub> hubs = hubRepository.findAllByDeletedAtIsNull();
        List<HubRoute> routes = hubRouteRepository.findAllByDeletedAtIsNull();
        Map<UUID, HubNode> nodes = new HashMap<>();

        for(Hub hub : hubs){
            nodes.put(
                    hub.getId(),
                    new HubNode(
                            hub.getId(),
                            hub.getLatitude(),
                            hub.getLongitude()
                    )
            );
        }

        for(HubRoute route : routes){
            HubNode fromNode = nodes.get(route.getFromHub().getId());
            HubNode toNode = nodes.get(route.getToHub().getId());

            if (fromNode == null || toNode == null) {
                log.warn("활성 허브를 찾을 수 없습니다. fromHubId={}", route.getFromHub().getId());
                continue;
            }

            fromNode.addEdge(
                    new Edge(
                            route.getToHub().getId(),
                            route.getDistance(),
                            route.getDuration()
                    )
            );
        }

        hubGraph = new HubGraph(nodes);
    }
}
