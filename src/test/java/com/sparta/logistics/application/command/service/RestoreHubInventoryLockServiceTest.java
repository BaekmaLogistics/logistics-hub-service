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

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
class RestoreHubInventoryLockServiceTest {

    @Mock
    private RedissonClient redissonClient;

    @Mock
    private RestoreHubInventoryService restoreHubInventoryService;

    @Mock
    private RLock lock;

    private RestoreHubInventoryLockService lockService;

    private UUID orderId;
    private UUID hubId;
    private UUID productId;

    @BeforeEach
    void setUp() {
        lockService = new RestoreHubInventoryLockService(
                redissonClient,
                restoreHubInventoryService
        );

        orderId = UUID.randomUUID();
        hubId = UUID.randomUUID();
        productId = UUID.randomUUID();
    }

    @Test
    @DisplayName("락 획득에 성공하면 재고를 복구하고 락을 해제한다.")
    void restore_success() throws InterruptedException {
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
        lockService.restore(
                orderId,
                hubId,
                productId
        );

        // then
        InOrder inOrder = inOrder(
                lock,
                restoreHubInventoryService
        );

        inOrder.verify(lock)
                .tryLock(3L, TimeUnit.SECONDS);

        inOrder.verify(restoreHubInventoryService)
                .restore(orderId, hubId, productId);

        inOrder.verify(lock)
                .isHeldByCurrentThread();

        inOrder.verify(lock)
                .unlock();
    }

    @Test
    @DisplayName("락 획득에 실패하면 재고를 복구하지 않는다.")
    void restore_lockAcquisitionFailed()
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
                () -> lockService.restore(
                        orderId,
                        hubId,
                        productId
                )
        );

        verifyNoInteractions(restoreHubInventoryService);

        verify(lock, never())
                .unlock();
    }

    @Test
    @DisplayName("재고 복구 중 예외가 발생해도 획득한 락을 해제한다.")
    void restore_unlockWhenRestoreFails()
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

        doThrow(new RuntimeException("재고 복구 실패"))
                .when(restoreHubInventoryService)
                .restore(orderId, hubId, productId);

        // when & then
        assertThrows(
                RuntimeException.class,
                () -> lockService.restore(
                        orderId,
                        hubId,
                        productId
                )
        );

        verify(restoreHubInventoryService)
                .restore(orderId, hubId, productId);

        verify(lock)
                .unlock();
    }

    @Test
    @DisplayName("재고 복구 커밋 후 락 해제에 실패해도 복구 요청은 실패로 처리하지 않는다.")
    void restore_unlockFailsAfterSuccess()
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

        doThrow(new RuntimeException("Redis connection failed"))
                .when(lock)
                .unlock();

        // when & then
        assertDoesNotThrow(
                () -> lockService.restore(
                        orderId,
                        hubId,
                        productId
                )
        );

        verify(restoreHubInventoryService)
                .restore(orderId, hubId, productId);

        verify(lock)
                .unlock();
    }

    @Test
    @DisplayName("락 대기 중 인터럽트가 발생하면 재고를 복구하지 않는다.")
    void restore_interrupted() throws InterruptedException {
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
                    () -> lockService.restore(
                            orderId,
                            hubId,
                            productId
                    )
            );

            verifyNoInteractions(restoreHubInventoryService);

            verify(lock, never())
                    .unlock();

        } finally {
            Thread.interrupted();
        }
    }
}