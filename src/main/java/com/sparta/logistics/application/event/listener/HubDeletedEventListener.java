package com.sparta.logistics.application.event.listener;

import com.sparta.logistics.application.event.HubDeletedEvent;
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

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(HubDeletedEvent event){
        log.info("허브 삭제 메시지 이벤트 발행: {}", event);
        integrationEventPublisher.publish(event);
    }
}
