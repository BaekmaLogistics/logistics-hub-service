package com.sparta.logistics.presentation.command.request;

import com.sparta.logistics.application.command.dto.hub.AssignHubManagerCommand;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssignHubManagerRequest {

    @NotNull
    private UUID managerId;

    public AssignHubManagerCommand toCommand(UUID hubId){
        return AssignHubManagerCommand.builder()
                .hubId(hubId)
                .managerId(managerId)
                .build();
    }
}
