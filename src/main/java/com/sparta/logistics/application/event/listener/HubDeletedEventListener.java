package com.sparta.logistics.application.event.listener;

import com.sparta.logistics.application.event.HubDeletedEvent;
import com.sparta.logistics.application.port.IntegrationEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class HubDeletedEventListener {

    private final IntegrationEventPublisher integrationEventPublisher;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(HubDeletedEvent event){
        integrationEventPublisher.publish(event);
    }
}
