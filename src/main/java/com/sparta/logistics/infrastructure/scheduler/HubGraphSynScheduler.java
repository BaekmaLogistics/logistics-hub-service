package com.sparta.logistics.infrastructure.scheduler;

import com.sparta.logistics.application.graph.HubGraphManager;
import com.sparta.logistics.application.port.GraphVersionStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class HubGraphSynScheduler {

    private final GraphVersionStore graphVersionStore;
    private final HubGraphManager hubGraphManager;

    private volatile boolean applicationReady = false;

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady(){
        applicationReady = true;

        log.info("애플리케이션 초기화 완료. 허브 그래프 동기화 스케줄러 활성화");
    }

    @Scheduled(fixedDelayString = "${hub.graph.sync-interval-ms:60000}")
    public void synchronize(){

        if(!applicationReady){
            return;
        }

        long sharedVersion = graphVersionStore.getCurrentVersion();
        long localVersion = hubGraphManager.getLocalGraphVersion();

        if(sharedVersion <= localVersion){
            return;
        }

        log.warn(
                "허브 그래프 버전 불일치 감지. sharedVersion={}, localVersion={}",
                sharedVersion,
                localVersion
        );

        hubGraphManager.reloadGraph(sharedVersion);

        log.info(
                "허브 그래프 재동기화 완료. graphVersion={}",
                sharedVersion
        );
    }
}
