package com.sparta.logistics.application.command.usecase;

import com.sparta.logistics.domain.entity.Hub;

public interface RefreshHubRouteUseCase {

    void refreshRoutesByHub(Hub hub);
}
