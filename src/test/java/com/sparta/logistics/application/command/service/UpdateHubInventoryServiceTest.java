package com.sparta.logistics.application.command.service;

import com.sparta.logistics.application.command.dto.hubinventory.UpdateHubInventoryCommand;
import com.sparta.logistics.application.command.dto.hubinventory.UpdateHubInventoryResponse;
import com.sparta.logistics.common.code.ErrorResponseCode;
import com.sparta.logistics.common.exception.ApiException;
import com.sparta.logistics.domain.entity.Hub;
import com.sparta.logistics.domain.entity.HubInventory;
import com.sparta.logistics.domain.repository.HubInventoryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import org.springframework.test.util.ReflectionTestUtils;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
class UpdateHubInventoryServiceTest {

    @Mock
    private HubInventoryRepository hubInventoryRepository;

    @InjectMocks
    private UpdateHubInventoryService updateHubInventoryService;

    @Test
    @DisplayName("허브 재고 수량 수정 성공")
    void update_success() {
        // given
        UUID inventoryId = UUID.randomUUID();
        UUID hubId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();

        Hub hub = Hub.builder()
                .name("서울 허브")
                .address("서울특별시")
                .latitude(37.1)
                .longitude(127.1)
                .build();

        ReflectionTestUtils.setField(hub, "id", hubId);

        HubInventory inventory = HubInventory.builder()
                .hub(hub)
                .productId(productId)
                .quantity(30)
                .build();

        ReflectionTestUtils.setField(
                inventory,
                "id",
                inventoryId
        );

        given(hubInventoryRepository.findById(inventoryId))
                .willReturn(Optional.of(inventory));

        UpdateHubInventoryCommand command =
                UpdateHubInventoryCommand.builder()
                        .id(inventoryId)
                        .quantity(10)
                        .build();

        // when
        UpdateHubInventoryResponse response =
                updateHubInventoryService.update(command);

        // then
        assertThat(response.getId()).isEqualTo(inventoryId);
        assertThat(response.getHubId()).isEqualTo(hubId);
        assertThat(response.getProductId()).isEqualTo(productId);
        assertThat(response.getQuantity()).isEqualTo(10);
        assertThat(response.getSafetyStock()).isEqualTo(20);

        // 실제 엔티티 상태도 변경되었는지 확인
        assertThat(inventory.getQuantity()).isEqualTo(10);

        verify(hubInventoryRepository).findById(inventoryId);
    }

    @Test
    @DisplayName("존재하지 않는 허브 재고 수정 시 예외가 발생한다")
    void update_fail_notFound() {
        // given
        UUID inventoryId = UUID.randomUUID();

        given(hubInventoryRepository.findById(inventoryId))
                .willReturn(Optional.empty());

        UpdateHubInventoryCommand command =
                UpdateHubInventoryCommand.builder()
                        .id(inventoryId)
                        .quantity(10)
                        .build();

        // when & then
        assertThatThrownBy(() ->
                updateHubInventoryService.update(command)
        )
                .isInstanceOf(ApiException.class)
                .hasMessage(
                        ErrorResponseCode.HUB_INVENTORY_NOT_FOUND.getMessage()
                );

        verify(hubInventoryRepository).findById(inventoryId);
    }

    @Test
    @DisplayName("삭제된 허브 재고 수정 시 예외가 발생한다")
    void update_fail_alreadyDeleted() {
        // given
        UUID inventoryId = UUID.randomUUID();

        Hub hub = Hub.builder()
                .name("서울 허브")
                .address("서울특별시")
                .latitude(37.1)
                .longitude(127.1)
                .build();

        HubInventory inventory = HubInventory.builder()
                .hub(hub)
                .productId(UUID.randomUUID())
                .quantity(30)
                .build();

        ReflectionTestUtils.setField(
                inventory,
                "deletedAt",
                Instant.now()
        );

        given(hubInventoryRepository.findById(inventoryId))
                .willReturn(Optional.of(inventory));

        UpdateHubInventoryCommand command =
                UpdateHubInventoryCommand.builder()
                        .id(inventoryId)
                        .quantity(10)
                        .build();

        // when & then
        assertThatThrownBy(() ->
                updateHubInventoryService.update(command)
        )
                .isInstanceOf(ApiException.class)
                .hasMessage(
                        ErrorResponseCode.HUB_INVENTORY_ALREADY_DELETED.getMessage()
                );

        // 수정되지 않았는지도 확인
        assertThat(inventory.getQuantity()).isEqualTo(30);
    }

    @Test
    @DisplayName("허브 재고 수량을 음수로 수정할 수 없다")
    void update_fail_negativeQuantity() {
        // given
        UUID inventoryId = UUID.randomUUID();

        Hub hub = Hub.builder()
                .name("서울 허브")
                .address("서울특별시")
                .latitude(37.1)
                .longitude(127.1)
                .build();

        HubInventory inventory = HubInventory.builder()
                .hub(hub)
                .productId(UUID.randomUUID())
                .quantity(30)
                .build();

        given(hubInventoryRepository.findById(inventoryId))
                .willReturn(Optional.of(inventory));

        UpdateHubInventoryCommand command =
                UpdateHubInventoryCommand.builder()
                        .id(inventoryId)
                        .quantity(-1)
                        .build();

        // when & then
        assertThatThrownBy(() ->
                updateHubInventoryService.update(command)
        )
                .isInstanceOf(ApiException.class)
                .hasMessage(
                        ErrorResponseCode.INVALID_STOCK_QUANTITY.getMessage()
                );

        // 예외 발생 후 기존 값 보존
        assertThat(inventory.getQuantity()).isEqualTo(30);
    }
}