package com.sparta.logistics.application.command.usecase;

import com.sparta.logistics.application.command.dto.hubroute.DeleteHubRouteCommand;

import java.util.UUID;

public interface DeleteHubRouteUseCase {

    void deleteHubRoute (DeleteHubRouteCommand command);
}
