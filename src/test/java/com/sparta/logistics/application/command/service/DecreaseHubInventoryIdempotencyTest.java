package com.sparta.logistics.application.command.service;

import com.sparta.logistics.application.command.usecase.DecreaseHubInventoryUseCase;
import com.sparta.logistics.common.exception.ApiException;
import com.sparta.logistics.domain.entity.Hub;
import com.sparta.logistics.domain.entity.HubInventory;
import com.sparta.logistics.domain.repository.HubInventoryRepository;
import com.sparta.logistics.domain.repository.HubRepository;
import com.sparta.logistics.support.IntegrationTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Tag("integration")
class DecreaseHubInventoryIdempotencyTest extends IntegrationTestSupport {

    @Autowired
    private DecreaseHubInventoryUseCase decreaseHubInventoryUseCase;

    @Autowired
    private HubInventoryRepository hubInventoryRepository;

    @Autowired
    private HubRepository hubRepository;

    @Test
    @DisplayName("동일한 주문의 재고 차감 요청이 반복되어도 재고는 한 번만 차감된다.")
    void decrease_sameRequestOnlyOnce() {

        // given
        UUID orderId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();

        Hub hub = Hub.builder()
                .name("멱등성 테스트 허브-" + UUID.randomUUID())
                .address("서울특별시 테스트 주소")
                .latitude(37.0)
                .longitude(127.0)
                .build();

        hub = hubRepository.saveAndFlush(hub);

        UUID hubId = hub.getId();

        HubInventory inventory = HubInventory.builder()
                .hub(hub)
                .productId(productId)
                .quantity(100)
                .safetyStock(20)
                .build();

        hubInventoryRepository.saveAndFlush(inventory);

        // when
        decreaseHubInventoryUseCase.decrease(
                orderId,
                hubId,
                productId,
                10
        );

        // 동일 요청 재시도
        decreaseHubInventoryUseCase.decrease(
                orderId,
                hubId,
                productId,
                10
        );

        // then
        HubInventory result =
                hubInventoryRepository.findById(inventory.getId())
                        .orElseThrow();

        assertEquals(90, result.getQuantity());
    }

    @Test
    @DisplayName("동일한 멱등성 키로 다른 수량을 요청하면 충돌 예외가 발생한다.")
    void decrease_sameIdempotencyKeyWithDifferentQuantity() {

        // given
        UUID orderId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();

        Hub hub = Hub.builder()
                .name("멱등성 충돌 테스트 허브-" + UUID.randomUUID())
                .address("서울특별시 테스트 주소")
                .latitude(37.0)
                .longitude(127.0)
                .build();

        hub = hubRepository.saveAndFlush(hub);

        UUID hubId = hub.getId();

        HubInventory inventory = HubInventory.builder()
                .hub(hub)
                .productId(productId)
                .quantity(100)
                .safetyStock(20)
                .build();

        hubInventoryRepository.saveAndFlush(inventory);

        // 최초 요청
        decreaseHubInventoryUseCase.decrease(
                orderId,
                hubId,
                productId,
                10
        );

        // when & then
        assertThrows(
                ApiException.class,
                () -> decreaseHubInventoryUseCase.decrease(
                        orderId,
                        hubId,
                        productId,
                        20
                )
        );

        HubInventory result =
                hubInventoryRepository.findById(inventory.getId())
                        .orElseThrow();

        assertEquals(90, result.getQuantity());
    }
}