//가장 가까운 노드부터 확정하면서 최단 거리로 갱신
package com.sparta.logistics.domain.graph;

import com.sparta.logistics.common.code.ErrorResponseCode;
import com.sparta.logistics.common.exception.ApiException;
import com.sparta.logistics.domain.model.ShortestPath;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class DijkstraPathFinder implements PathFinder {

    @Override
    public ShortestPath findShortestPath(
            HubGraph hubGraph,
            UUID fromHubId,
            UUID toHubId
    ){
        //자료 구조 생성
        Map<UUID, Double> distances = new HashMap<>();
        Map<UUID, Integer> durations = new HashMap<>();
        Map<UUID, UUID> previous = new HashMap<>();
        PriorityQueue<PathState> pq = new PriorityQueue<>();

        //초기화
        for(HubNode node : hubGraph.getNodes()){
            distances.put(node.getHubId(), Double.MAX_VALUE);
            durations.put(node.getHubId(), Integer.MAX_VALUE);
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
                            cur.hubId()
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

        //경로 복원
        LinkedList<UUID> path = new LinkedList<>();

        UUID current = toHubId;

        while(current != null){
            path.addFirst(current);
            current = previous.get(current);
        }

        if(distances.get(toHubId) == Double.MAX_VALUE){
            throw new ApiException(ErrorResponseCode.PATH_NOT_FOUND);
        }

        return new ShortestPath(
                path,
                distances.get(toHubId),
                durations.get(toHubId)
        );
    }
}
