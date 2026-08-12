package com.sparta.logistics.infrastructure.feign.fallback;

import com.sparta.logistics.common.code.ErrorResponseCode;
import com.sparta.logistics.common.exception.ApiException;
import com.sparta.logistics.infrastructure.feign.client.ProductClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ProductClientFallbackFactory implements FallbackFactory<ProductClient> {

    @Override
    public ProductClient create(Throwable cause){
        return productId -> {
            log.error(
                    "Product Service 호출 실패. productId={}",
                    productId,
                    cause
            );

            throw new ApiException(ErrorResponseCode.EXTERNAL_SERVICE_UNAVAILABLE);
        };
    }
}
