package com.sparta.logistics.application.command.usecase;

import com.sparta.logistics.application.command.dto.hubroute.UpdateHubRouteCommand;
import com.sparta.logistics.application.command.dto.hubroute.UpdateHubRouteResponse;

public interface UpdateHubRouteUseCase {

    UpdateHubRouteResponse updateHubRoute(
            UpdateHubRouteCommand command
    );
}
