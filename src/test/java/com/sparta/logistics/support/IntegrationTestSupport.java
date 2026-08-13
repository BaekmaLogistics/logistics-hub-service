package com.sparta.logistics.support;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@SpringBootTest(properties = {
        "spring.cloud.config.enabled=false",
        "eureka.client.enabled=false",

        "naver.maps.url=https://example.com",
        "naver.maps.client-id=test-client-id",
        "naver.maps.client-secret=test-client-secret",

        "spring.rabbitmq.host=localhost",
        "spring.rabbitmq.port=5672",
        "spring.rabbitmq.username=guest",
        "spring.rabbitmq.password=guest"
})
@ActiveProfiles("test")
public abstract class IntegrationTestSupport {

    @ServiceConnection
    protected static final PostgreSQLContainer<?> POSTGRES;

    protected static final GenericContainer<?> REDIS;

    static{
        POSTGRES = new PostgreSQLContainer<>("postgres:16");
        POSTGRES.start();

        REDIS = new GenericContainer<>(
                DockerImageName.parse("redis:7-alpine")
        ).withExposedPorts(6379);

        REDIS.start();
    }

    @DynamicPropertySource
    static void configureRedis(DynamicPropertyRegistry registry) {
        registry.add(
                "spring.data.redis.host",
                REDIS::getHost
        );

        registry.add(
                "spring.data.redis.port",
                () -> REDIS.getMappedPort(6379)
        );
    }
}