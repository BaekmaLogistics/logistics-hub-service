package com.sparta.logistics.infrastructure.feign.adapter;

import com.sparta.logistics.application.port.ProductValidator;
import com.sparta.logistics.infrastructure.feign.client.ProductClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ProductFeignAdapter implements ProductValidator {

    private final ProductClient productClient;

    @Override
    public void validateExists(UUID productId){
        productClient.getProduct(productId);
    }
}
