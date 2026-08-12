package com.sparta.logistics.application.command.service;

import com.sparta.logistics.domain.entity.Hub;
import com.sparta.logistics.domain.entity.HubRoute;
import com.sparta.logistics.domain.repository.HubRouteRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
class DeleteHubRoutesServiceTest {

    @Mock
    private HubRouteRepository hubRouteRepository;

    @InjectMocks
    private DeleteHubRoutesService deleteHubRoutesService;

    @Test
    @DisplayName("허브 삭제 시 해당 허브와 연결된 활성 경로들을 모두 soft delete한다")
    void deleteRoutesByHub_success() {
        // given
        UUID deletedBy = UUID.randomUUID();

        Hub seoulHub = Hub.builder()
                .name("서울 허브")
                .address("서울특별시")
                .latitude(37.5665)
                .longitude(126.9780)
                .build();

        Hub daejeonHub = Hub.builder()
                .name("대전 허브")
                .address("대전광역시")
                .latitude(36.3504)
                .longitude(127.3845)
                .build();

        Hub incheonHub = Hub.builder()
                .name("인천 허브")
                .address("인천광역시")
                .latitude(37.4563)
                .longitude(126.7052)
                .build();

        // 서울이 출발점인 Route
        HubRoute seoulToDaejeon = HubRoute.builder()
                .fromHub(seoulHub)
                .toHub(daejeonHub)
                .distance(150.0)
                .duration(120)
                .build();

        // 서울이 도착점인 Route
        HubRoute incheonToSeoul = HubRoute.builder()
                .fromHub(incheonHub)
                .toHub(seoulHub)
                .distance(50.0)
                .duration(60)
                .build();

        given(hubRouteRepository.findAllActiveRoutesByHub(seoulHub))
                .willReturn(List.of(
                        seoulToDaejeon,
                        incheonToSeoul
                ));

        // when
        deleteHubRoutesService.deleteRoutesByHub(
                seoulHub,
                deletedBy
        );

        // then
        assertThat(seoulToDaejeon.isDeleted()).isTrue();
        assertThat(incheonToSeoul.isDeleted()).isTrue();

        assertThat(seoulToDaejeon.getDeletedBy())
                .isEqualTo(deletedBy);

        assertThat(incheonToSeoul.getDeletedBy())
                .isEqualTo(deletedBy);

        verify(hubRouteRepository)
                .findAllActiveRoutesByHub(seoulHub);
    }
}