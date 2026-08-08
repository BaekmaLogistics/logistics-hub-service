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
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Tag("integration")
@SpringBootTest
class DecreaseHubInventoryConcurrencyTest {

    @Autowired
    private DecreaseHubInventoryUseCase decreaseHubInventoryUseCase;

    @Autowired
    private HubInventoryRepository hubInventoryRepository;

    @Autowired
    private HubRepository hubRepository;

    @Test
    @DisplayName("동시에 재고를 차감하면 최종 재고 수량이 정확해야 한다")
    void decrease_concurrently() throws InterruptedException {
        UUID hubId = UUID.fromString(
                "330f5e07-1bb8-45f0-84f5-03db26562caa"
        );

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

        int threadCount = 10;

        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);

        CountDownLatch readyLatch = new CountDownLatch(threadCount);

        CountDownLatch startLatch = new CountDownLatch(1);

        CountDownLatch doneLatch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    readyLatch.countDown();

                    startLatch.await();

                    decreaseHubInventoryUseCase.decrease(
                            hubId,
                            productId,
                            1
                    );
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    doneLatch.countDown();
                }
            });
        }

        //모든 스레드가 실행 준비될 때까지 대기
        readyLatch.await();

        //동시에 실행
        startLatch.countDown();;

        //종료 대기
        doneLatch.await();

        HubInventory result = hubInventoryRepository.findById(inventory.getId())
                .orElseThrow();

        System.out.println("예상 수량 = 90, 실제 수량 = "+result.getQuantity());

        assertEquals(90, result.getQuantity());

        executorService.shutdown();
    }
}
