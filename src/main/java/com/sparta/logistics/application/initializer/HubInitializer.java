package com.sparta.logistics.application.initializer;

import com.sparta.logistics.application.command.dto.hub.CreateHubCommand;
import com.sparta.logistics.application.command.usecase.CreateHubUseCase;
import com.sparta.logistics.application.initializer.seed.HubSeed;
import com.sparta.logistics.domain.repository.HubRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Order(1)
public class HubInitializer implements ApplicationRunner {

    private final CreateHubUseCase createHubUseCase;
    private final HubRepository hubRepository;

    @Override
    public void run(ApplicationArguments args) {

        if(hubRepository.count() == HubSeed.values().length){
            return;
        }

        if (hubRepository.count() > 0) {
            throw new IllegalStateException(
                    "허브 초기 데이터가 일부만 존재합니다. DB를 확인해주세요."
            );
        }

        for(HubSeed hubSeed : HubSeed.values()){

            CreateHubCommand command = CreateHubCommand.builder()
                    .name(hubSeed.getName())
                    .address(hubSeed.getAddress())
                    .build();

            createHubUseCase.createHub(command);
        }
    }
}
