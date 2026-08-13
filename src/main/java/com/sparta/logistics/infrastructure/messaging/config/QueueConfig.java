package com.sparta.logistics.infrastructure.messaging.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QueueConfig {
    @Value("${message.exchange}")
    private String exchange;

    @Value("${message.queue.delivery}")
    private String queueDelivery;
    @Value("${message.queue.hub}")
    private String queueHub;
    @Value("${message.queue.notification}")
    private String queueNotification;
    @Value("${message.queue.company}")
    private String queueCompany;
    @Value("${message.queue.order}")
    private String queueOrder;

    @Value("${message.binding-key.notification.inventory-low}")
    private String keyNotificationInventoryLow;
    @Value("${message.binding-key.notification.order-created}")
    private String keyNotificationOrderCreated;
    @Value("${message.binding-key.notification.order-canceled}")
    private String keyNotificationOrderCanceled;
    @Value("${message.binding-key.notification.order-completed}")
    private String keyNotificationOrderCompleted;
    @Value("${message.binding-key.hub.route-changed}")
    private String keyHubRouteChanged;
    @Value("${message.binding-key.company.hub-deleted}")
    private String keyCompanyHubDeleted;
    @Value("${message.binding-key.order.inventory-deducted}")
    private String keyOrderInventoryDeducted;
    @Value("${message.binding-key.order.inventory-deduct-failed}")
    private String keyOrderInventoryDeductFailed;
    @Value("${message.binding-key.order.inventory-restored}")
    private String keyOrderInventoryRestored;
    @Value("${message.binding-key.order.inventory-restore-failed}")
    private String keyOrderInventoryRestoreFailed;
    @Value("${message.binding-key.order.delivery-created}")
    private String keyOrderDeliveryCreated;
    @Value("${message.binding-key.order.delivery-create-failed}")
    private String keyOrderDeliveryCreateFailed;
    @Value("${message.binding-key.order.delivery-canceled}")
    private String keyOrderDeliveryCanceled;
    @Value("${message.binding-key.order.delivery-cancel-failed}")
    private String keyOrderDeliveryCancelFailed;

    @Bean
    public TopicExchange exchange() { return new TopicExchange(exchange); }

    @Bean public Queue queueDelivery() { return new Queue(queueDelivery); }
    @Bean public Queue queueHub() { return new Queue(queueHub); }
    @Bean public Queue queueNotification() { return new Queue(queueNotification); }
    @Bean public Queue queueCompany() { return new Queue(queueCompany); }
    @Bean public Queue queueOrder() { return new Queue(queueOrder); }

    // Hub -> Notification (재고 부족)
    @Bean
    public Binding bindingNotificationInventoryLow() {
        return BindingBuilder.bind(queueNotification())
                .to(exchange())
                .with(keyNotificationInventoryLow);
    }

    // Order -> Notification (주문 생성)
    @Bean
    public Binding bindingNotificationOrderCreated() {
        return BindingBuilder.bind(queueNotification())
                .to(exchange())
                .with(keyNotificationOrderCreated);
    }

    // Order -> Notification (주문 취소)
    @Bean
    public Binding bindingNotificationOrderCanceled() {
        return BindingBuilder.bind(queueNotification())
                .to(exchange())
                .with(keyNotificationOrderCanceled);
    }

    // Order -> Notification (주문 완료)
    @Bean
    public Binding bindingNotificationOrderCompleted() {
        return BindingBuilder.bind(queueNotification())
                .to(exchange())
                .with(keyNotificationOrderCompleted);
    }

    // Hub -> Hub (허브 경로 변경)
    @Bean
    public Binding bindingHubRouteChanged() {
        return BindingBuilder.bind(queueHub())
                .to(exchange())
                .with(keyHubRouteChanged);
    }

    // Hub -> Company (허브 삭제)
    @Bean
    public Binding bindingCompanyHubDeleted() {
        return BindingBuilder.bind(queueCompany())
                .to(exchange())
                .with(keyCompanyHubDeleted);
    }

    // Hub -> Order (재고 차감 성공)
    @Bean
    public Binding bindingOrderInventoryDeducted() {
        return BindingBuilder.bind(queueOrder())
                .to(exchange())
                .with(keyOrderInventoryDeducted);
    }

    // Hub -> Order (재고 차감 실패)
    @Bean
    public Binding bindingOrderInventoryDeductFailed() {
        return BindingBuilder.bind(queueOrder())
                .to(exchange())
                .with(keyOrderInventoryDeductFailed);
    }

    // Hub -> Order (재고 복구 성공)
    @Bean
    public Binding bindingOrderInventoryRestored() {
        return BindingBuilder.bind(queueOrder())
                .to(exchange())
                .with(keyOrderInventoryRestored);
    }

    // Hub -> Order (재고 복구 실패)
    @Bean
    public Binding bindingOrderInventoryRestoreFailed() {
        return BindingBuilder.bind(queueOrder())
                .to(exchange())
                .with(keyOrderInventoryRestoreFailed);
    }

    // Delivery -> Order (배송 생성 성공)
    @Bean
    public Binding bindingOrderDeliveryCreated() {
        return BindingBuilder.bind(queueOrder())
                .to(exchange())
                .with(keyOrderDeliveryCreated);
    }

    // Delivery -> Order (배송 생성 실패)
    @Bean
    public Binding bindingOrderDeliveryCreateFailed() {
        return BindingBuilder.bind(queueOrder())
                .to(exchange())
                .with(keyOrderDeliveryCreateFailed);
    }

    // Delivery -> Order (배송 취소 성공)
    @Bean
    public Binding bindingOrderDeliveryCanceled() {
        return BindingBuilder.bind(queueOrder())
                .to(exchange())
                .with(keyOrderDeliveryCanceled);
    }

    // Delivery -> Order (배송 취소 실패)
    @Bean
    public Binding bindingOrderDeliveryCancelFailed() {
        return BindingBuilder.bind(queueOrder())
                .to(exchange())
                .with(keyOrderDeliveryCancelFailed);
    }

}
