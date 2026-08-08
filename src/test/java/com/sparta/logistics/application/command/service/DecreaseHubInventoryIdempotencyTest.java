package com.sparta.logistics.application.command.service;

import com.sparta.logistics.application.command.usecase.DecreaseHubInventoryUseCase;
import com.sparta.logistics.domain.entity.Hub;
import com.sparta.logistics.domain.entity.HubInventory;
import com.sparta.logistics.domain.repository.HubInventoryRepository;
import com.sparta.logistics.domain.repository.HubRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Tag("integration")
@SpringBootTest
class DecreaseHubInventoryIdempotencyTest {

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
        UUID hubId = UUID.fromString(
                "330f5e07-1bb8-45f0-84f5-03db26562caa"
        );

        UUID orderId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();

        Hub hub = hubRepository.findById(hubId)
                .orElseThrow();

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

        System.out.println(
                "동일 요청 2회 호출 - 예상 수량 = 90, 실제 수량 = "
                        + result.getQuantity()
        );

        assertEquals(90, result.getQuantity());
    }
}