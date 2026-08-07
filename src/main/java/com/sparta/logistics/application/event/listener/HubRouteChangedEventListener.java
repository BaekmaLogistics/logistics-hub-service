package com.sparta.logistics.application.event.listener;

import com.sparta.logistics.application.event.HubRouteChangedEvent;
import com.sparta.logistics.application.graph.HubGraphManager;
import com.sparta.logistics.application.port.ShortestPathCache;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class HubRouteChangedEventListener {

    private final HubGraphManager hubGraphManager;
    private final ShortestPathCache shortestPathCache;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(HubRouteChangedEvent event){
        hubGraphManager.reloadGraph();
        shortestPathCache.evictAll();
    }
}
