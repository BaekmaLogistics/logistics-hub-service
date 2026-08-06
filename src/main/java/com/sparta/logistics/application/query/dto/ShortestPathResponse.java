package com.sparta.logistics.application.query.dto;

import com.sparta.logistics.domain.model.ShortestPath;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ShortestPathResponse {
    private List<UUID> hubIds;

    private double totalDistance;

    private int totalDuration;

    public static ShortestPathResponse from(ShortestPath shortestPath){
        return new ShortestPathResponse(
                shortestPath.path(),
                shortestPath.totalDistance(),
                shortestPath.totalDuration()
        );
    }
}
