package com.sparta.logistics.application.command.usecase;

import com.sparta.logistics.application.command.dto.hub.AssignHubManagerCommand;
import com.sparta.logistics.application.command.dto.hub.AssignHubManagerResponse;
import org.springframework.expression.spel.ast.Assign;

public interface AssignHubManagerUseCase {

    AssignHubManagerResponse assign(AssignHubManagerCommand command);
}
