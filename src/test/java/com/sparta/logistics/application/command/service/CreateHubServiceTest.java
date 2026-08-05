package com.sparta.logistics.application.command.service;

import com.sparta.logistics.application.command.dto.hub.CreateHubCommand;
import com.sparta.logistics.application.command.dto.hub.CreateHubResponse;
import com.sparta.logistics.application.common.dto.Coordinate;
import com.sparta.logistics.application.common.service.GeocodingService;
import com.sparta.logistics.common.code.ErrorResponseCode;
import com.sparta.logistics.common.exception.ApiException;
import com.sparta.logistics.domain.entity.Hub;
import com.sparta.logistics.domain.repository.HubRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
class CreateHubServiceTest {

    @Mock
    HubRepository hubRepository;

    @Mock
    GeocodingService geocodingService;

    @InjectMocks
    CreateHubService createHubService;

    @Test
    @DisplayName("허브 생성")
    void createHub_success(){

        CreateHubCommand command = CreateHubCommand.builder()
                .name("서울 허브")
                .address("서울특별시 송파구 송파대로 55")
                .build();

        Coordinate coordinate = new Coordinate(
                37.474154,
                127.123906
        );

        when(hubRepository.existsByName(command.getName()))
                .thenReturn(false);

        when(geocodingService.getCoordinate(command.getAddress()))
                .thenReturn(coordinate);

        when(hubRepository.save(any(Hub.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // when
        CreateHubResponse response = createHubService.createHub(command);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo(command.getName());
        assertThat(response.getAddress()).isEqualTo(command.getAddress());
        assertThat(response.getLatitude()).isEqualTo(37.474154);
        assertThat(response.getLongitude()).isEqualTo(127.123906);

        verify(hubRepository).existsByName(command.getName());
        verify(geocodingService).getCoordinate(command.getAddress());

        ArgumentCaptor<Hub> captor = ArgumentCaptor.forClass(Hub.class);

        verify(hubRepository).save(captor.capture());

        Hub savedHub = captor.getValue();

        assertThat(savedHub.getName()).isEqualTo(command.getName());
        assertThat(savedHub.getAddress()).isEqualTo(command.getAddress());
        assertThat(savedHub.getLatitude()).isEqualTo(37.474154);
        assertThat(savedHub.getLongitude()).isEqualTo(127.123906);
    }

    @Test
    @DisplayName("이미 존재하는 허브명이면 예외가 발생한다.")
    void createHub_fail_duplicateName() {
        // given
        CreateHubCommand command = CreateHubCommand.builder()
                .name("서울 허브")
                .address("서울특별시 송파구 송파대로 55")
                .build();

        when(hubRepository.existsByName(command.getName()))
                .thenReturn(true);

        // when & then
        ApiException exception = assertThrows(
                ApiException.class,
                () -> createHubService.createHub(command)
        );

        assertThat(exception.getResponseCode())
                .isEqualTo(ErrorResponseCode.HUB_ALREADY_EXISTS);

        verify(hubRepository).existsByName(command.getName());
        verify(hubRepository, never()).save(any());
        verifyNoInteractions(geocodingService);
    }

}