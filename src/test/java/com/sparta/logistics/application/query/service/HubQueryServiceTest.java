package com.sparta.logistics.application.query.service;

import com.sparta.logistics.application.query.dto.HubDetailResponse;
import com.sparta.logistics.application.query.dto.HubSearchCondition;
import com.sparta.logistics.common.code.ErrorResponseCode;
import com.sparta.logistics.common.exception.ApiException;
import com.sparta.logistics.domain.entity.Hub;
import com.sparta.logistics.domain.repository.HubRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
class HubQueryServiceTest {

    @Mock
    HubRepository hubRepository;

    @InjectMocks
    HubQueryService hubQueryService;

    @Test
    @DisplayName("허브 단건 조회")
    void get_hub_success(){
        UUID hubId = UUID.randomUUID();

        Hub hub = Hub.create(
                "서울 허브",
                "서울특별시 송파구",
                37.5145,
                127.1059
        );

        when(hubRepository.findById(hubId))
                .thenReturn(Optional.of(hub));

        HubDetailResponse response = hubQueryService.getHub(hubId);

        assertThat(response.getName()).isEqualTo("서울 허브");
        assertThat(response.getAddress()).isEqualTo("서울특별시 송파구");
        assertThat(response.getLatitude()).isEqualTo(37.5145);
        assertThat(response.getLongitude()).isEqualTo(127.1059);

        verify(hubRepository).findById(hubId);
    }

    @Test
    @DisplayName("존재하지 않는 허브를 조회할 때 예외가 발생한다.")
    void get_hub_fail() {
        UUID hubId = UUID.randomUUID();

        when(hubRepository.findById(hubId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> hubQueryService.getHub(hubId))
                .isInstanceOf(ApiException.class)
                .hasMessage(ErrorResponseCode.HUB_NOT_FOUND.getMessage());

        verify(hubRepository).findById(hubId);
    }

    @Test
    @DisplayName("조건에 맞는 허브 목록 조회하기")
    void search_hubs_success(){
        HubSearchCondition condition = new HubSearchCondition("서울", null);
        Pageable pageable = PageRequest.of(0, 10);

        Hub hub = Hub.create(
                "서울 허브",
                "서울특별시 송파구",
                37.5145,
                127.1059
        );

        Page<Hub> hubPage = new PageImpl<>(List.of(hub), pageable, 1);

        when(hubRepository.search(
                condition.getName(),
                condition.getAddress(),
                pageable
        )).thenReturn(hubPage);

        Page<HubDetailResponse> result = hubQueryService.searchHubs(condition, pageable);

        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getName()).isEqualTo("서울 허브");

        verify(hubRepository).search(
                condition.getName(),
                condition.getAddress(),
                pageable
        );
    }

    @Test
    @DisplayName("조회 결과가 없으면 빈 페이지 반환")
    void search_hubs_empty(){
        HubSearchCondition condition = new HubSearchCondition(null, null);
        Pageable pageable = PageRequest.of(0,10);

        Page<Hub> emptyPage = Page.empty(pageable);

        when(hubRepository.search(
                condition.getName(),
                condition.getAddress(),
                pageable
        )).thenReturn(emptyPage);

        Page<HubDetailResponse> result =
                hubQueryService.searchHubs(condition, pageable);

        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isZero();

        verify(hubRepository).search(
                condition.getName(),
                condition.getAddress(),
                pageable
        );
    }
}