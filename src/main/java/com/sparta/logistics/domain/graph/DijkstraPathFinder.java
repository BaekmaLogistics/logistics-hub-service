//가장 가까운 노드부터 확정하면서 최단 거리로 갱신
package com.sparta.logistics.domain.graph;

import com.sparta.logistics.common.code.ErrorResponseCode;
import com.sparta.logistics.common.exception.ApiException;
import com.sparta.logistics.domain.model.PathSegment;
import com.sparta.logistics.domain.model.ShortestPath;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;

@Slf4j
@Component
public class DijkstraPathFinder implements PathFinder {

    private record PreviousEdge(
            UUID fromHubId,
            Edge edge
    ){
    }

    @Override
    public ShortestPath findShortestPath(
            HubGraph hubGraph,
            UUID fromHubId,
            UUID toHubId
    ){
        //자료 구조 생성
        Map<UUID, Double> distances = new HashMap<>();
        Map<UUID, Integer> durations = new HashMap<>();
        Map<UUID, PreviousEdge> previous = new HashMap<>();
        PriorityQueue<PathState> pq = new PriorityQueue<>();

        //초기화
        for(HubNode node : hubGraph.getNodes()){
            distances.put(node.getHubId(), Double.MAX_VALUE);
            durations.put(node.getHubId(), Integer.MAX_VALUE);
        }

        log.info(
                "최단경로 탐색 시작. from={}, to={}, nodeCount={}, fromExists={}, toExists={}",
                fromHubId,
                toHubId,
                distances.size(),
                distances.containsKey(fromHubId),
                distances.containsKey(toHubId)
        );

        if(!distances.containsKey(fromHubId) || !distances.containsKey(toHubId)){
            throw new ApiException(ErrorResponseCode.PATH_NOT_FOUND);
        }

        //시작 노드 넣기
        distances.put(fromHubId, 0.0);
        durations.put(fromHubId, 0);
        pq.offer(new PathState(fromHubId, 0.0));

        //PQ 반복
        while(!pq.isEmpty()){
            PathState cur = pq.poll();

            //최소 거리만 들어가게 보장
            if(cur.distance() > distances.get(cur.hubId())){
                continue;
            }

            if(cur.hubId().equals(toHubId)){
                break;
            }

            HubNode curNode = hubGraph.getNode(cur.hubId());

            if(curNode == null){
                continue;
            }

            //Relaxation
            for(Edge edge : curNode.getEdges()){
                double newDis = distances.get(cur.hubId()) + edge.getDistance();
                int newDur = durations.get(cur.hubId())+edge.getDuration();

                if(newDis < distances.get(edge.getToHubId())){
                    distances.put(
                            edge.getToHubId(),
                            newDis
                    );

                    durations.put(
                            edge.getToHubId(),
                            newDur
                    );

                    previous.put(
                            edge.getToHubId(),
                            new PreviousEdge(cur.hubId(), edge)
                    );

                    pq.offer(
                            new PathState(
                                    edge.getToHubId(),
                                    newDis
                            )
                    );
                }
            }
        }

        log.info(
                "최단경로 탐색 완료. from={}, to={}, distance={}",
                fromHubId,
                toHubId,
                distances.get(toHubId)
        );

        if(distances.get(toHubId) == Double.MAX_VALUE){
            throw new ApiException(ErrorResponseCode.PATH_NOT_FOUND);
        }

        //경로 복원
        LinkedList<UUID> path = new LinkedList<>();
        LinkedList<PathSegment> segments = new LinkedList<>();

        UUID current = toHubId;
        path.addFirst(current);

        while(!current.equals(fromHubId)){
            PreviousEdge previousEdge = previous.get(current);

            if(previousEdge == null){
                throw new ApiException(ErrorResponseCode.PATH_NOT_FOUND);
            }

            Edge edge = previousEdge.edge();

            segments.addFirst(
                    new PathSegment(
                            previousEdge.fromHubId(),
                            current,
                            edge.getDistance(),
                            edge.getDuration()
                    )
            );

            current = previousEdge.fromHubId();
            path.addFirst(current);
        }

        return new ShortestPath(
                path,
                segments,
                distances.get(toHubId),
                durations.get(toHubId)
        );
    }
}
