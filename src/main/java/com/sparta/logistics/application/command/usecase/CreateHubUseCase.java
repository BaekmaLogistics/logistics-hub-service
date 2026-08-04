package com.sparta.logistics.application.command.usecase;

import com.sparta.logistics.application.command.dto.hub.CreateHubCommand;
import com.sparta.logistics.application.command.dto.hub.CreateHubResponse;

public interface CreateHubUseCase {

    CreateHubResponse createHub(CreateHubCommand command);
}
