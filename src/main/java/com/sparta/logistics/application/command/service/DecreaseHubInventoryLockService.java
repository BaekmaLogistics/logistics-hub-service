package com.sparta.logistics.application.command.service;

import com.sparta.logistics.application.command.usecase.DecreaseHubInventoryUseCase;
import com.sparta.logistics.common.code.ErrorResponseCode;
import com.sparta.logistics.common.exception.ApiException;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class DecreaseHubInventoryLockService implements DecreaseHubInventoryUseCase{

    private static final long WAIT_TIME = 3L;

    private final RedissonClient redissonClient;
    private final DecreaseHubInventoryService decreaseHubInventoryService;

    private String createLockKey(UUID hubId, UUID productId){
        return "lock:hub-inventory:" + hubId+":"+productId;
    }

    @Override
    public void decrease(UUID hubId, UUID productId, int quantity){
        String lockKey = createLockKey(hubId, productId);
        RLock lock = redissonClient.getLock(lockKey);

        boolean acquired = false;

        try{
            acquired = lock.tryLock(WAIT_TIME, TimeUnit.SECONDS);

            if(!acquired){
                throw new ApiException(ErrorResponseCode.INVENTORY_LOCK_ACQUISITION_FAILED);
            }

            decreaseHubInventoryService.decrease(hubId, productId, quantity);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();;
            throw new ApiException(ErrorResponseCode.INVENTORY_LOCK_ACQUISITION_FAILED);
        } finally {
            if(acquired && lock.isHeldByCurrentThread()){
                lock.unlock();
            }
        }
    }
}
