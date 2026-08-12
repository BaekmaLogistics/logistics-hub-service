package com.sparta.logistics.application.event.listener;

import com.sparta.logistics.application.event.HubDeletedEvent;
import com.sparta.logistics.application.port.GraphVersionStore;
import com.sparta.logistics.application.port.IntegrationEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class HubDeletedEventListener {

    private final IntegrationEventPublisher integrationEventPublisher;
    private final GraphVersionStore graphVersionStore;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(HubDeletedEvent event){

        //허브가 삭제됐어!
        //그래프에서도 허브와 관련 경로 삭제되니까 버전 올리자!
        graphVersionStore.increment();

        log.info("허브 삭제 메시지 이벤트 발행: {}", event);
        integrationEventPublisher.publish(event);
    }
}
