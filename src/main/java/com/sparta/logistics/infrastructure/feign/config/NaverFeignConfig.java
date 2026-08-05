package com.sparta.logistics.infrastructure.feign.config;

import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NaverFeignConfig {

    @Value("${naver.maps.client-id}")
    private String clientId;

    @Value("${naver.maps.client-secret}")
    private String clientSecret;

    @Bean
    public RequestInterceptor naverRequestInterceptor() {
        return requestTemplate -> {

            requestTemplate.header("x-ncp-apigw-api-key-id", clientId);
            requestTemplate.header("x-ncp-apigw-api-key", clientSecret);
        };
    }
}
