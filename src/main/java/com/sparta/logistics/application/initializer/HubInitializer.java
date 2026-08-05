package com.sparta.logistics.application.initializer;

import com.sparta.logistics.application.command.dto.hub.CreateHubCommand;
import com.sparta.logistics.application.command.usecase.CreateHubUseCase;
import com.sparta.logistics.application.initializer.seed.HubSeed;
import com.sparta.logistics.domain.entity.Hub;
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

    private void validateHubSeedConsistency(){
        for(HubSeed hubSeed : HubSeed.values()){
            Hub hub = hubRepository.findByName(hubSeed.getName())
                    .orElseThrow(() ->
                            new IllegalStateException(
                                    "필수 허브가 누락되었습니다. name=" + hubSeed.getName()
                            ));

            if (!hub.getAddress().equals(hubSeed.getAddress())) {
                throw new IllegalStateException(
                        "허브 시드 데이터가 일치하지 않습니다. name=" + hubSeed.getName()
                );
            }
        }
    }

    private void initializeHubs(){
        for (HubSeed hubSeed : HubSeed.values()) {

            CreateHubCommand command = CreateHubCommand.builder()
                    .name(hubSeed.getName())
                    .address(hubSeed.getAddress())
                    .build();

            createHubUseCase.createHub(command);
        }
    }

    @Override
    public void run(ApplicationArguments args) {
        if (hubRepository.count() == 0) {
            initializeHubs();
            return;
        }

        validateHubSeedConsistency();
    }
}
