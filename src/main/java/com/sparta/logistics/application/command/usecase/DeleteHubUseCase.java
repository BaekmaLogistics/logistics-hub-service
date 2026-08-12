package com.sparta.logistics.application.command.usecase;

import com.sparta.logistics.application.command.dto.hub.DeleteHubCommand;

public interface DeleteHubUseCase {
    void deleteHub(DeleteHubCommand command);
}
