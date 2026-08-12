package com.sparta.logistics.infrastructure.cache;

import com.sparta.logistics.application.port.GraphVersionStore;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisGraphVersionStore implements GraphVersionStore {

    private static final String GRAPH_VERSION_KEY = "hub:graph:version";

    private final StringRedisTemplate redisTemplate;

    @Override
    public long increment(){
        Long version = redisTemplate.opsForValue().increment(GRAPH_VERSION_KEY);

        if(version == null){
            throw new IllegalStateException("허브 그래프 버전 증가에 실패했습니다.");
        }

        return version;
    }

    @Override
    public long getCurrentVersion(){
        String version = redisTemplate.opsForValue().get(GRAPH_VERSION_KEY);

        return version == null ? 0L :Long.parseLong(version);
    }
}
