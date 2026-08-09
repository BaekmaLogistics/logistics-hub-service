package com.sparta.logistics.application.event.listener;

import com.sparta.logistics.application.event.HubDeletedEvent;
import com.sparta.logistics.application.event.InventoryLowEvent;
import com.sparta.logistics.application.port.IntegrationEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class InventoryLowEventListener {

    private IntegrationEventPublisher integrationEventPublisher;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(InventoryLowEvent event){
        integrationEventPublisher.publish(event);
    }
}
