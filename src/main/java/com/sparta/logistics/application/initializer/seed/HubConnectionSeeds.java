package com.sparta.logistics.application.initializer.seed;

import java.util.List;

public final class HubConnectionSeeds {

    private HubConnectionSeeds() {}

    public static final List<HubConnectionSeed> SEEDS = List.of(

            // 경기 남부
            new HubConnectionSeed(HubSeed.GYEONGGI_SOUTH, HubSeed.GYEONGGI_NORTH),
            new HubConnectionSeed(HubSeed.GYEONGGI_SOUTH, HubSeed.SEOUL),
            new HubConnectionSeed(HubSeed.GYEONGGI_SOUTH, HubSeed.INCHEON),
            new HubConnectionSeed(HubSeed.GYEONGGI_SOUTH, HubSeed.GANGWON),
            new HubConnectionSeed(HubSeed.GYEONGGI_SOUTH, HubSeed.GYEONGBUK),
            new HubConnectionSeed(HubSeed.GYEONGGI_SOUTH, HubSeed.DAEJEON),
            new HubConnectionSeed(HubSeed.GYEONGGI_SOUTH, HubSeed.DAEGU),

            // 대전
            new HubConnectionSeed(HubSeed.DAEJEON, HubSeed.CHUNGNAM),
            new HubConnectionSeed(HubSeed.DAEJEON, HubSeed.CHUNGBUK),
            new HubConnectionSeed(HubSeed.DAEJEON, HubSeed.SEJONG),
            new HubConnectionSeed(HubSeed.DAEJEON, HubSeed.JEONBUK),
            new HubConnectionSeed(HubSeed.DAEJEON, HubSeed.GWANGJU),
            new HubConnectionSeed(HubSeed.DAEJEON, HubSeed.JEONNAM),

            // 대구
            new HubConnectionSeed(HubSeed.DAEGU, HubSeed.GYEONGBUK),
            new HubConnectionSeed(HubSeed.DAEGU, HubSeed.GYEONGNAM),
            new HubConnectionSeed(HubSeed.DAEGU, HubSeed.BUSAN),
            new HubConnectionSeed(HubSeed.DAEGU, HubSeed.ULSAN),
            new HubConnectionSeed(HubSeed.DAEGU, HubSeed.DAEJEON)

    );
}
