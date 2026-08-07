package com.sparta.logistics.infrastructure.cache;

import com.sparta.logistics.application.port.ShortestPathCache;
import com.sparta.logistics.domain.model.ShortestPath;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@Tag("integrationTest")
@SpringBootTest
class RedisShortestPathCacheTest {

    @Autowired
    private ShortestPathCache shortestPathCache;

    @Test
    @DisplayName("최단 경로 캐시를 전체 삭제한다")
    void evictAll() {
        UUID hubA = UUID.randomUUID();
        UUID hubB = UUID.randomUUID();
        UUID hubC = UUID.randomUUID();

        ShortestPath pathAB = new ShortestPath(
                List.of(hubA, hubB),
                100.0,
                60
        );

        ShortestPath pathBC = new ShortestPath(
                List.of(hubB, hubC),
                200.0,
                120
        );

        shortestPathCache.put(hubA, hubB, pathAB);
        shortestPathCache.put(hubB, hubC, pathBC);

        // when
        shortestPathCache.evictAll();

        // then
        assertTrue(
                shortestPathCache.get(hubA, hubB).isEmpty()
        );

        assertTrue(
                shortestPathCache.get(hubB, hubC).isEmpty()
        );
    }
}