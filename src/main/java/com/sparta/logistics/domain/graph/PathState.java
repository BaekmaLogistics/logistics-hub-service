package com.sparta.logistics.domain.graph;

import java.util.UUID;

public record PathState(
        UUID hubId,
        double distance
) implements Comparable<PathState>{

    @Override
    public int compareTo(PathState other){
        return Double.compare(distance, other.distance);
    }
}
