package com.sparta.logistics.application.query.usecase;

import com.sparta.logistics.application.query.dto.ShortestPathResponse;

import java.util.UUID;

public interface FindShortestPathUseCase {

    ShortestPathResponse findShortestPath(
            UUID fromHubId,
            UUID toHubId
    );
}
