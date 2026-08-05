package com.sparta.logistics.infrastructure.feign.service;

import com.sparta.logistics.application.common.service.DirectionService;
import com.sparta.logistics.domain.entity.Hub;
import com.sparta.logistics.infrastructure.feign.client.NaverDirectionClient;
import com.sparta.logistics.infrastructure.feign.dto.direction.DirectionResponse;
import com.sparta.logistics.infrastructure.feign.dto.direction.RouteInfo;
import com.sparta.logistics.infrastructure.feign.dto.direction.Summary;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NaverDirectionService implements DirectionService {

    private final NaverDirectionClient directionClient;

    @Override
    public RouteInfo getRoute(Hub fromHub, Hub toHub){
        String start = fromHub.getLongitude()+","+ fromHub.getLatitude();
        String goal = toHub.getLongitude()+","+ toHub.getLatitude();

        DirectionResponse response = directionClient.getDirection(start, goal, "trafast");

        Summary summary = response.getSummary();

        return RouteInfo.builder()
                .distance(summary.getDistance()/1000.0)
                .duration((int)(summary.getDuration()/1000/60))
                .build();
    }
}
