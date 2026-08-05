package com.sparta.logistics.application.command.usecase;

import com.sparta.logistics.application.command.dto.hub.UpdateHubCommand;
import com.sparta.logistics.application.command.dto.hub.UpdateHubResponse;

import java.util.UUID;

public interface UpdateHubUseCase {

    UpdateHubResponse updateHub(UpdateHubCommand command);
}
