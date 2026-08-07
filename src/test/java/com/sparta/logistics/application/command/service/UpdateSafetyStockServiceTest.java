package com.sparta.logistics.application.command.service;

import com.sparta.logistics.application.command.dto.hubinventory.UpdateSafetyStockCommand;
import com.sparta.logistics.application.command.dto.hubinventory.UpdateSafetyStockResponse;
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
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
class UpdateSafetyStockServiceTest {

    @Mock
    private HubInventoryRepository hubInventoryRepository;

    @InjectMocks
    private UpdateSafetyStockService updateSafetyStockService;

    @Test
    @DisplayName("안전재고 설정 성공")
    void updateSafetyStock_success() {
        // given
        UUID inventoryId = UUID.randomUUID();

        HubInventory inventory = createInventory(inventoryId);

        UpdateSafetyStockCommand command =
                UpdateSafetyStockCommand.builder()
                        .inventoryId(inventoryId)
                        .safetyStock(50)
                        .build();

        given(hubInventoryRepository.findById(inventoryId))
                .willReturn(Optional.of(inventory));

        // when
        UpdateSafetyStockResponse response =
                updateSafetyStockService.updateSafetyStock(command);

        // then
        assertThat(response.getInventoryId()).isEqualTo(inventoryId);
        assertThat(response.getSafetyStock()).isEqualTo(50);
        assertThat(inventory.getSafetyStock()).isEqualTo(50);

        verify(hubInventoryRepository).findById(inventoryId);
        verifyNoMoreInteractions(hubInventoryRepository);
    }

    @Test
    @DisplayName("존재하지 않는 허브 재고의 안전재고를 설정할 수 없다")
    void updateSafetyStock_fail_inventoryNotFound() {
        // given
        UUID inventoryId = UUID.randomUUID();

        UpdateSafetyStockCommand command =
                UpdateSafetyStockCommand.builder()
                        .inventoryId(inventoryId)
                        .safetyStock(50)
                        .build();

        given(hubInventoryRepository.findById(inventoryId))
                .willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() ->
                updateSafetyStockService.updateSafetyStock(command)
        )
                .isInstanceOf(ApiException.class)
                .hasMessage(
                        ErrorResponseCode.HUB_INVENTORY_NOT_FOUND.getMessage()
                );
    }

    @Test
    @DisplayName("삭제된 허브 재고의 안전재고를 설정할 수 없다")
    void updateSafetyStock_fail_deletedInventory() {
        // given
        UUID inventoryId = UUID.randomUUID();

        HubInventory inventory = createInventory(inventoryId);
        inventory.softDelete(UUID.randomUUID());

        UpdateSafetyStockCommand command =
                UpdateSafetyStockCommand.builder()
                        .inventoryId(inventoryId)
                        .safetyStock(50)
                        .build();

        given(hubInventoryRepository.findById(inventoryId))
                .willReturn(Optional.of(inventory));

        // when & then
        assertThatThrownBy(() ->
                updateSafetyStockService.updateSafetyStock(command)
        )
                .isInstanceOf(ApiException.class)
                .hasMessage(
                        ErrorResponseCode.HUB_INVENTORY_ALREADY_DELETED.getMessage()
                );

        assertThat(inventory.getSafetyStock()).isEqualTo(20);
    }

    @Test
    @DisplayName("안전재고는 음수로 설정할 수 없다")
    void updateSafetyStock_fail_negativeSafetyStock() {
        // given
        UUID inventoryId = UUID.randomUUID();

        HubInventory inventory = createInventory(inventoryId);

        UpdateSafetyStockCommand command =
                UpdateSafetyStockCommand.builder()
                        .inventoryId(inventoryId)
                        .safetyStock(-1)
                        .build();

        given(hubInventoryRepository.findById(inventoryId))
                .willReturn(Optional.of(inventory));

        // when & then
        assertThatThrownBy(() ->
                updateSafetyStockService.updateSafetyStock(command)
        )
                .isInstanceOf(ApiException.class)
                .hasMessage(
                        ErrorResponseCode.INVALID_SAFETY_STOCK.getMessage()
                );

        // 실패했으므로 기존 값 유지
        assertThat(inventory.getSafetyStock()).isEqualTo(20);
    }

    private HubInventory createInventory(UUID inventoryId) {

        Hub hub = Hub.builder()
                .name("서울 허브")
                .address("서울")
                .latitude(37.1)
                .longitude(127.1)
                .build();

        HubInventory inventory = HubInventory.builder()
                .hub(hub)
                .productId(UUID.randomUUID())
                .quantity(100)
                .safetyStock(20)
                .build();

        ReflectionTestUtils.setField(
                inventory,
                "id",
                inventoryId
        );

        return inventory;
    }
}