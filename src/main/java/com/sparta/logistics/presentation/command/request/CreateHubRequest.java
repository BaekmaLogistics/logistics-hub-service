//HTTP 요청을 표현하는 객체 -> controller만 알고 있음
package com.sparta.logistics.presentation.command.request;

import com.sparta.logistics.application.command.dto.hub.CreateHubCommand;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CreateHubRequest {

    @NotBlank(message = "허브명은 필수입니다.")
    private String name;

    @NotBlank(message = "주소는 필수입니다.")
    private String address;

    @NotNull(message = "허브 관리자는 필수입니다.")
    private UUID managerId;

    public CreateHubCommand toCommand() {
        return CreateHubCommand.builder()
                .name(name)
                .address(address)
                .managerId(managerId)
                .build();
    }
}
