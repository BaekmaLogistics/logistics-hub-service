package com.sparta.logistics.application.initializer;

import com.sparta.logistics.application.command.dto.hubroute.CreateHubRouteCommand;
import com.sparta.logistics.application.command.usecase.CreateHubRouteUseCase;
import com.sparta.logistics.application.initializer.seed.HubConnectionSeed;
import com.sparta.logistics.application.initializer.seed.HubConnectionSeeds;
import com.sparta.logistics.common.code.ErrorResponseCode;
import com.sparta.logistics.common.exception.ApiException;
import com.sparta.logistics.domain.entity.Hub;
import com.sparta.logistics.domain.repository.HubRepository;
import com.sparta.logistics.domain.repository.HubRouteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Component
@Order(2)
@RequiredArgsConstructor
public class HubRouteInitializer implements ApplicationRunner {

    private final HubRepository hubRepository;
    private final HubRouteRepository hubRouteRepository;
    private final CreateHubRouteUseCase createHubRouteUseCase;

    private void createRoute(Hub fromHub, Hub toHub){

        if(hubRouteRepository.existsByFromHubIdAndToHubId(
                fromHub.getId(),
                toHub.getId()
        )){
            return;
        }

        createHubRouteUseCase.create(
                CreateHubRouteCommand.builder()
                        .fromHubId(fromHub.getId())
                        .toHubId(toHub.getId())
                        .build()
        );
    }

    private Hub getHub(Map<String, Hub> hubMap, String hubName) {
        Hub hub = hubMap.get(hubName);

        if (hub == null) {
            throw new ApiException(ErrorResponseCode.HUB_NOT_FOUND);
        }

        return hub;
    }

    @Override
    public void run(ApplicationArguments args){

        Map<String, Hub> hubMap = hubRepository.findAll()
                .stream()
                .collect(Collectors.toMap(
                        Hub::getName,
                        Function.identity()
                ));

        for (HubConnectionSeed seed : HubConnectionSeeds.SEEDS){
            try{
                Hub fromHub = getHub(hubMap, seed.fromHub().getName());
                Hub toHub = getHub(hubMap, seed.toHub().getName());

                createRoute(fromHub, toHub);
                createRoute(toHub, fromHub);
            } catch (Exception e) {

                throw new IllegalStateException(
                        String.format(
                                "허브 연결 초기화 실패 (%s -> %s)",
                                seed.fromHub().getName(),
                                seed.toHub().getName()
                        ),
                        e
                );
            }

        }
    }
}
