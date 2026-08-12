package com.sparta.logistics.application.port;

import com.sparta.logistics.application.event.HubDeletedEvent;
import com.sparta.logistics.application.event.HubRouteChangedIntegrationEvent;
import com.sparta.logistics.application.event.InventoryLowEvent;

public interface IntegrationEventPublisher {

    void publish(InventoryLowEvent event);

    void publish(HubDeletedEvent event);

    void publish(HubRouteChangedIntegrationEvent event);
}
