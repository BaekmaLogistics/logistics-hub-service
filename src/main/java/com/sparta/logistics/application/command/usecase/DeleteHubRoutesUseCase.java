package com.sparta.logistics.application.command.usecase;

import com.sparta.logistics.domain.entity.Hub;

import java.util.UUID;

public interface DeleteHubRoutesUseCase {

    void deleteRoutesByHub(
            Hub hub,
            UUID deletedBy
    );
}
