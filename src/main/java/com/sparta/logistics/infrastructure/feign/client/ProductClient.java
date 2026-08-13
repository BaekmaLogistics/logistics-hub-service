package com.sparta.logistics.infrastructure.feign.client;

import com.sparta.logistics.infrastructure.feign.config.OpenFeignConfig;
import com.sparta.logistics.infrastructure.feign.dto.product.ProductResponse;
import com.sparta.logistics.infrastructure.feign.fallback.ProductClientFallbackFactory;
import com.sparta.logistics.presentation.common.dto.response.GeneralResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(
        name = "company-product-service",
        fallbackFactory = ProductClientFallbackFactory.class
)
public interface ProductClient {

    @GetMapping("/internal/api/v1/products/{productId}")
    GeneralResponse<ProductResponse> getProduct(
            @PathVariable("productId")UUID productId
            );
}
