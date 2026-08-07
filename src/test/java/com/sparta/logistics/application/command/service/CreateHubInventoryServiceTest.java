package com.sparta.logistics.application.command.service;

import com.sparta.logistics.application.command.dto.hubinventory.CreateHubInventoryCommand;
import com.sparta.logistics.application.command.dto.hubinventory.CreateHubInventoryResponse;
import com.sparta.logistics.common.code.ErrorResponseCode;
import com.sparta.logistics.common.exception.ApiException;
import com.sparta.logistics.domain.entity.Hub;
import com.sparta.logistics.domain.entity.HubInventory;
import com.sparta.logistics.domain.repository.HubInventoryRepository;
import com.sparta.logistics.domain.repository.HubRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
class CreateHubInventoryServiceTest {

    @Mock
    private HubInventoryRepository hubInventoryRepository;

    @Mock
    private HubRepository hubRepository;

    @InjectMocks
    private CreateHubInventoryService createHubInventoryService;

    @Test
    @DisplayName("허브 재고 등록 성공")
    void create_success() {
        // given
        UUID hubId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();

        Hub hub = Hub.builder()
                .name("서울 허브")
                .address("서울")
                .latitude(37.1)
                .longitude(127.1)
                .build();

        CreateHubInventoryCommand command =
                CreateHubInventoryCommand.builder()
                        .hubId(hubId)
                        .productId(productId)
                        .quantity(100)
                        .build();

        given(hubRepository.findById(hubId))
                .willReturn(Optional.of(hub));

        given(hubInventoryRepository
                .existsByHubAndProductIdAndDeletedAtIsNull(
                        hub,
                        productId
                ))
                .willReturn(false);

        given(hubInventoryRepository.save(any(HubInventory.class)))
                .willAnswer(invocation -> invocation.getArgument(0));

        // when
        CreateHubInventoryResponse response =
                createHubInventoryService.create(command);

        // then
        assertThat(response.getHubId()).isEqualTo(hubId);
        assertThat(response.getProductId()).isEqualTo(productId);
        assertThat(response.getQuantity()).isEqualTo(100);

        // 기본 안전재고 정책 검증
        assertThat(response.getSafetyStock()).isEqualTo(20);

        verify(hubInventoryRepository)
                .save(any(HubInventory.class));
    }

    @Test
    @DisplayName("존재하지 않는 허브에는 재고를 등록할 수 없다")
    void create_fail_hubNotFound() {
        UUID hubId = UUID.randomUUID();

        CreateHubInventoryCommand command =
                CreateHubInventoryCommand.builder()
                        .hubId(hubId)
                        .productId(UUID.randomUUID())
                        .quantity(100)
                        .build();

        given(hubRepository.findById(hubId))
                .willReturn(Optional.empty());

        assertThatThrownBy(() ->
                createHubInventoryService.create(command)
        )
                .isInstanceOf(ApiException.class)
                .hasMessage(ErrorResponseCode.HUB_NOT_FOUND.getMessage());

        verifyNoInteractions(hubInventoryRepository);
    }

    @Test
    @DisplayName("삭제된 허브에는 재고를 등록할 수 없다")
    void create_fail_deletedHub() {
        UUID hubId = UUID.randomUUID();

        Hub hub = Hub.builder()
                .name("서울 허브")
                .address("서울")
                .latitude(37.1)
                .longitude(127.1)
                .build();

        hub.softDelete(UUID.randomUUID());

        given(hubRepository.findById(hubId))
                .willReturn(Optional.of(hub));

        CreateHubInventoryCommand command =
                CreateHubInventoryCommand.builder()
                        .hubId(hubId)
                        .productId(UUID.randomUUID())
                        .quantity(100)
                        .build();

        assertThatThrownBy(() ->
                createHubInventoryService.create(command)
        )
                .isInstanceOf(ApiException.class)
                .hasMessage(
                        ErrorResponseCode.HUB_ALREADY_DELETED.getMessage()
                );

        verifyNoInteractions(hubInventoryRepository);
    }

    @Test
    @DisplayName("동일 허브와 상품의 활성 재고가 존재하면 중복 등록할 수 없다")
    void create_fail_alreadyExists() {
        UUID hubId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();

        Hub hub = Hub.builder()
                .name("서울 허브")
                .address("서울")
                .latitude(37.1)
                .longitude(127.1)
                .build();

        given(hubRepository.findById(hubId))
                .willReturn(Optional.of(hub));

        given(hubInventoryRepository
                .existsByHubAndProductIdAndDeletedAtIsNull(
                        hub,
                        productId
                ))
                .willReturn(true);

        CreateHubInventoryCommand command =
                CreateHubInventoryCommand.builder()
                        .hubId(hubId)
                        .productId(productId)
                        .quantity(100)
                        .build();

        assertThatThrownBy(() ->
                createHubInventoryService.create(command)
        )
                .isInstanceOf(ApiException.class)
                .hasMessage(
                        ErrorResponseCode.HUB_INVENTORY_ALREADY_EXISTS.getMessage()
                );

        verify(hubInventoryRepository, never())
                .save(any(HubInventory.class));
    }

    @Test
    @DisplayName("음수 수량으로 허브 재고를 등록할 수 없다")
    void create_fail_negativeQuantity() {
        UUID hubId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();

        Hub hub = Hub.builder()
                .name("서울 허브")
                .address("서울")
                .latitude(37.1)
                .longitude(127.1)
                .build();

        given(hubRepository.findById(hubId))
                .willReturn(Optional.of(hub));

        given(hubInventoryRepository
                .existsByHubAndProductIdAndDeletedAtIsNull(
                        hub,
                        productId
                ))
                .willReturn(false);

        CreateHubInventoryCommand command =
                CreateHubInventoryCommand.builder()
                        .hubId(hubId)
                        .productId(productId)
                        .quantity(-1)
                        .build();

        assertThatThrownBy(() ->
                createHubInventoryService.create(command)
        )
                .isInstanceOf(ApiException.class)
                .hasMessage(
                        ErrorResponseCode.INVALID_STOCK_QUANTITY.getMessage()
                );

        verify(hubInventoryRepository, never())
                .save(any(HubInventory.class));
    }
}