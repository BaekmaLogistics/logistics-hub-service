package com.sparta.logistics.infrastructure.cache;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.logistics.application.port.ShortestPathCache;
import com.sparta.logistics.domain.model.ShortestPath;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisShortestPathCache implements ShortestPathCache {

    private static final String KEY_PREFIX = "shortest-route:";
    private static final Duration TTL = Duration.ofMinutes(10);

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public Optional<ShortestPath> get(
            UUID fromHubId,
            UUID toHubId
    ) {
        String key = generateKey(fromHubId, toHubId);

        Object value = redisTemplate.opsForValue().get(key);

        if(value == null){
            log.info("MISS key={}", key);
            return Optional.empty();
        }

        log.info("HIT key={}", key);

        ShortestPath shortestPath =
                objectMapper.convertValue(value, ShortestPath.class);

        return Optional.of(shortestPath);
    }

    @Override
    public void put(
            UUID fromHubId,
            UUID toHubId,
            ShortestPath shortestPath
    ){
        String key = generateKey(fromHubId, toHubId);

        redisTemplate.opsForValue()
                .set(key, shortestPath, TTL);

        log.info("PUT key={}", key);
    }

    @Override
    public void evictAll(){

        // TODO: 그래프 버전 기반 캐시 정합성 보장
        // - evictAll 실패 시 이전 그래프 기준 캐시가 조회될 수 있음
        // - 이전 HubGraph로 계산 중인 요청이 evict 이후 stale cache를 다시 저장할 수 있음
        // - HubGraph version을 캐시 key namespace에 포함하여 해결 예정
        //   ex) shortest-route:{graphVersion}:{fromHubId}:{toHubId}

        ScanOptions options = ScanOptions.scanOptions()
                .match(KEY_PREFIX+"*")
                .count(100)
                .build();

        try(Cursor<String> cursor = redisTemplate.scan(options)){
            while(cursor.hasNext()){
                redisTemplate.delete(cursor.next());
            }
        }
    }

    private String generateKey(UUID fromHubId, UUID toHubId){
        return KEY_PREFIX + fromHubId + ":" + toHubId;
    }
}
