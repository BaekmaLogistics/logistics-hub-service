package com.sparta.logistics.infrastructure.cache;

import com.sparta.logistics.application.port.ShortestPathCache;
import com.sparta.logistics.domain.model.PathSegment;
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
    @DisplayName("그래프 버전이 변경되면 이전 버전의 최단 경로 캐시는 조회되지 않는다")
    void cacheIsSeparatedByGraphVersion() {
        // given
        long oldVersion = 1L;
        long newVersion = 2L;

        UUID hubA = UUID.randomUUID();
        UUID hubB = UUID.randomUUID();

        PathSegment pathSegment = new PathSegment(
                hubA,
                hubB,
                100.0,
                60
        );

        ShortestPath shortestPath = new ShortestPath(
                List.of(hubA, hubB),
                List.of(pathSegment),
                100.0,
                60
        );

        // 이전 그래프 버전으로 캐시 저장
        shortestPathCache.put(
                oldVersion,
                hubA,
                hubB,
                shortestPath
        );

        // when & then

        // 같은 버전에서는 HIT
        assertTrue(
                shortestPathCache.get(
                        oldVersion,
                        hubA,
                        hubB
                ).isPresent()
        );

        // 새로운 그래프 버전에서는 MISS
        assertTrue(
                shortestPathCache.get(
                        newVersion,
                        hubA,
                        hubB
                ).isEmpty()
        );
    }
}