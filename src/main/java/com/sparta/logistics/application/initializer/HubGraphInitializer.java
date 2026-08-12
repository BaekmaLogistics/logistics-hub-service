package com.sparta.logistics.application.initializer;

import com.sparta.logistics.application.graph.HubGraphManager;
import com.sparta.logistics.application.port.GraphVersionStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Profile("!test")
@RequiredArgsConstructor
public class HubGraphInitializer {

    private final HubGraphManager hubGraphManager;
    private final GraphVersionStore graphVersionStore;

    @EventListener(ApplicationReadyEvent.class)
    public void initialize(){
        long currentVersion = graphVersionStore.getCurrentVersion();

        log.info(
                "애플리케이션 초기화 완료 후 허브 그래프 초기 적재 시작. graphVersion={}",
                currentVersion
        );

        hubGraphManager.reloadGraph(currentVersion);

        log.info(
                "허브 그래프 초기 적재 완료. graphVersion={}",
                currentVersion
        );
    }
}
