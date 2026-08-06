package com.sparta.logistics.presentation.query.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FindShortestPathRequest {

    @NotNull
    private UUID fromHubId;

    @NotNull
    private UUID toHubId;
}
