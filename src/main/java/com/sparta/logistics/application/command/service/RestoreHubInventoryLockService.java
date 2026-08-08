package com.sparta.logistics.application.command.service;

import com.sparta.logistics.application.command.usecase.RestoreHubInventoryUseCase;
import com.sparta.logistics.common.code.ErrorResponseCode;
import com.sparta.logistics.common.exception.ApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class RestoreHubInventoryLockService implements RestoreHubInventoryUseCase {

    private final long WAIT_TIME = 3L;

    private final RedissonClient redissonClient;
    private final RestoreHubInventoryService restoreHubInventoryService;

    private String createLockKey(UUID hubId, UUID productId){
        return "lock:hub-inventory:"+hubId+":"+productId;
    }

    @Override
    public void restore(UUID orderId, UUID hubId, UUID productId){
        String lockKey = createLockKey(hubId, productId);
        RLock lock = redissonClient.getLock(lockKey);

        boolean acquired = false;

        try{
            acquired = lock.tryLock(
                    WAIT_TIME,
                    TimeUnit.SECONDS
            );

            if(!acquired){
                throw new ApiException(
                        ErrorResponseCode.INVENTORY_LOCK_ACQUISITION_FAILED
                );
            }

            restoreHubInventoryService.restore(
                    orderId,
                    hubId,
                    productId
            );
        } catch (InterruptedException e){
            Thread.currentThread().interrupt();

            throw new ApiException(
                    ErrorResponseCode.INVENTORY_LOCK_ACQUISITION_FAILED
            );
        } finally {
            if(acquired){
                try{
                    if(lock.isHeldByCurrentThread()){
                        lock.unlock();
                    }
                } catch (Exception e){
                    log.error(
                            "재고 복구 완료 후 분산락 해제 실패. lockKey={}",
                            lock,
                            e
                    );
                }
            }
        }
    }
}
