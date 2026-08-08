package com.sparta.logistics.infrastructure.lock.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(name = "spring.data.redis.host")
public class RedissonConfig {

    @Bean(destroyMethod = "shutdown") //애플리케이션 종료 시 Redisson이 가지고 있는 리소스 정리
    public RedissonClient redissonClient(
            @Value("${spring.data.redis.host}") String host,
            @Value("${spring.data.redis.port}") int port
    ) {
        Config config = new Config();

        //현재 레디스 단일 서버 사용하니까 Singer Server 모드로
        config.useSingleServer().setAddress("redis://"+host+":"+port);

        return Redisson.create(config);
    }

}
