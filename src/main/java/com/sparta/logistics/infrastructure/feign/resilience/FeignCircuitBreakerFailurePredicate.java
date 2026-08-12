package com.sparta.logistics.infrastructure.feign.resilience;

import com.sparta.logistics.infrastructure.feign.exception.FeignApiException;

import java.util.function.Predicate;

public class FeignCircuitBreakerFailurePredicate implements Predicate<Throwable> {

    @Override
    public boolean test(Throwable throwable){
        if(throwable instanceof FeignApiException exception){
            return exception.getStatus() >= 500;
        }

        return true;
    }
}
