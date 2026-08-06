package com.sparta.logistics.application.command.usecase;

import com.sparta.logistics.application.command.dto.hubroute.CreateHubRouteCommand;
import com.sparta.logistics.application.command.dto.hubroute.CreateHubRouteResponse;

public interface CreateHubRouteUseCase {

    CreateHubRouteResponse create(CreateHubRouteCommand command);
}
