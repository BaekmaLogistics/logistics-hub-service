package com.sparta.logistics.application.command.dto.hubinventory;

import com.sparta.logistics.domain.model.UserRole;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.query.sql.internal.ParameterRecognizerImpl;

import java.util.UUID;

@Getter
@Builder
public class CreateHubInventoryCommand {

    private final UUID hubId;
    private final UUID productId;
    private final Integer quantity;

    private final UUID requesterId;
    private final UserRole requesterRole;
}
