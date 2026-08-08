package com.sparta.logistics.application.command.service;

import com.sparta.logistics.common.exception.ApiException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
class DecreaseHubInventoryLockServiceTest {

    @Mock
    private RedissonClient redissonClient;

    @Mock
    private DecreaseHubInventoryService decreaseHubInventoryService;

    @Mock
    private RLock lock;

    private DecreaseHubInventoryLockService lockService;

    private UUID hubId;
    private UUID productId;

    @BeforeEach
    void setUp() {
        lockService = new DecreaseHubInventoryLockService(
                redissonClient,
                decreaseHubInventoryService
        );

        hubId = UUID.randomUUID();
        productId = UUID.randomUUID();
    }

    @Test
    @DisplayName("락 획득에 성공하면 재고를 차감하고 락을 해제한다.")
    void decrease_success() throws InterruptedException {
        // given
        String lockKey =
                "lock:hub-inventory:" + hubId + ":" + productId;

        when(redissonClient.getLock(lockKey))
                .thenReturn(lock);

        when(lock.tryLock(3L, TimeUnit.SECONDS))
                .thenReturn(true);

        when(lock.isHeldByCurrentThread())
                .thenReturn(true);

        // when
        lockService.decrease(
                hubId,
                productId,
                10
        );

        // then
        InOrder inOrder = inOrder(
                lock,
                decreaseHubInventoryService
        );

        inOrder.verify(lock)
                .tryLock(3L, TimeUnit.SECONDS);

        inOrder.verify(decreaseHubInventoryService)
                .decrease(hubId, productId, 10);

        inOrder.verify(lock)
                .isHeldByCurrentThread();

        inOrder.verify(lock)
                .unlock();
    }

    @Test
    @DisplayName("락 획득에 실패하면 재고를 차감하지 않는다.")
    void decrease_lockAcquisitionFailed()
            throws InterruptedException {
        // given
        String lockKey =
                "lock:hub-inventory:" + hubId + ":" + productId;

        when(redissonClient.getLock(lockKey))
                .thenReturn(lock);

        when(lock.tryLock(3L, TimeUnit.SECONDS))
                .thenReturn(false);

        // when & then
        assertThrows(
                ApiException.class,
                () -> lockService.decrease(
                        hubId,
                        productId,
                        10
                )
        );

        verifyNoInteractions(decreaseHubInventoryService);

        verify(lock, never())
                .unlock();
    }

    @Test
    @DisplayName("락 대기 중 인터럽트가 발생하면 재고를 차감하지 않는다.")
    void decrease_interrupted() throws InterruptedException {
        // given
        String lockKey =
                "lock:hub-inventory:" + hubId + ":" + productId;

        when(redissonClient.getLock(lockKey))
                .thenReturn(lock);

        when(lock.tryLock(3L, TimeUnit.SECONDS))
                .thenThrow(new InterruptedException());

        try {
            // when & then
            assertThrows(
                    ApiException.class,
                    () -> lockService.decrease(
                            hubId,
                            productId,
                            10
                    )
            );

            verifyNoInteractions(decreaseHubInventoryService);

            verify(lock, never())
                    .unlock();

        } finally {
            // 테스트 실행 스레드의 interrupt 상태 정리
            Thread.interrupted();
        }
    }

    @Test
    @DisplayName("재고 차감 중 예외가 발생해도 획득한 락을 해제한다.")
    void decrease_unlockWhenDecreaseFails()
            throws InterruptedException {
        // given
        String lockKey =
                "lock:hub-inventory:" + hubId + ":" + productId;

        when(redissonClient.getLock(lockKey))
                .thenReturn(lock);

        when(lock.tryLock(3L, TimeUnit.SECONDS))
                .thenReturn(true);

        when(lock.isHeldByCurrentThread())
                .thenReturn(true);

        doThrow(new RuntimeException("재고 차감 실패"))
                .when(decreaseHubInventoryService)
                .decrease(hubId, productId, 10);

        // when & then
        assertThrows(
                RuntimeException.class,
                () -> lockService.decrease(
                        hubId,
                        productId,
                        10
                )
        );

        verify(lock).unlock();
    }
}