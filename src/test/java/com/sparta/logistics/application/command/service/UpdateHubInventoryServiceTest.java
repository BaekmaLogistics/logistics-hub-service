package com.sparta.logistics.application.command.service;

import com.sparta.logistics.application.command.dto.hubinventory.UpdateHubInventoryCommand;
import com.sparta.logistics.application.command.dto.hubinventory.UpdateHubInventoryResponse;
import com.sparta.logistics.application.common.validator.HubAccessValidator;
import com.sparta.logistics.application.event.InventoryLowEvent;
import com.sparta.logistics.common.code.ErrorResponseCode;
import com.sparta.logistics.common.exception.ApiException;
import com.sparta.logistics.domain.entity.Hub;
import com.sparta.logistics.domain.entity.HubInventory;
import com.sparta.logistics.domain.model.UserRole;
import com.sparta.logistics.domain.repository.HubInventoryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
class UpdateHubInventoryServiceTest {

    @Mock
    private HubInventoryRepository hubInventoryRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Mock
    private HubAccessValidator hubAccessValidator;

    @InjectMocks
    private UpdateHubInventoryService updateHubInventoryService;

    @Test
    @DisplayName("허브 재고 수량 수정으로 안전재고 이하가 되면 재고 부족 이벤트를 발행한다")
    void update_success() {
        // given
        UUID inventoryId = UUID.randomUUID();
        UUID hubId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        UUID requesterId = UUID.randomUUID();
        UserRole requesterRole = UserRole.HUB_MANAGER;

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

        ReflectionTestUtils.setField(inventory, "id", inventoryId);

        given(hubInventoryRepository.findById(inventoryId))
                .willReturn(Optional.of(inventory));

        UpdateHubInventoryCommand command =
                UpdateHubInventoryCommand.builder()
                        .id(inventoryId)
                        .quantity(10)
                        .requesterId(requesterId)
                        .requesterRole(requesterRole)
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

        assertThat(inventory.getQuantity()).isEqualTo(10);

        verify(hubInventoryRepository)
                .findById(inventoryId);

        verify(hubAccessValidator)
                .validate(
                        hub,
                        requesterId,
                        requesterRole
                );

        ArgumentCaptor<InventoryLowEvent> eventCaptor =
                ArgumentCaptor.forClass(InventoryLowEvent.class);

        verify(eventPublisher)
                .publishEvent(eventCaptor.capture());

        InventoryLowEvent event = eventCaptor.getValue();

        assertThat(event.inventoryId()).isEqualTo(inventoryId);
        assertThat(event.hubId()).isEqualTo(hubId);
        assertThat(event.productId()).isEqualTo(productId);
        assertThat(event.quantity()).isEqualTo(10);
        assertThat(event.safetyStock()).isEqualTo(20);
        assertThat(event.occurredAt()).isNotNull();
    }

    @Test
    @DisplayName("안전재고 이하 상태에서 재고를 다시 낮춰도 재고 부족 이벤트를 중복 발행하지 않는다")
    void update_lowStockToLowStock_doesNotPublishEvent() {
        // given
        UUID inventoryId = UUID.randomUUID();
        UUID requesterId = UUID.randomUUID();
        UserRole requesterRole = UserRole.HUB_MANAGER;

        Hub hub = Hub.builder()
                .name("서울 허브")
                .address("서울특별시")
                .latitude(37.1)
                .longitude(127.1)
                .build();

        HubInventory inventory = HubInventory.builder()
                .hub(hub)
                .productId(UUID.randomUUID())
                .quantity(15)
                .build();

        given(hubInventoryRepository.findById(inventoryId))
                .willReturn(Optional.of(inventory));

        UpdateHubInventoryCommand command =
                UpdateHubInventoryCommand.builder()
                        .id(inventoryId)
                        .quantity(10)
                        .requesterId(requesterId)
                        .requesterRole(requesterRole)
                        .build();

        // when
        updateHubInventoryService.update(command);

        // then
        assertThat(inventory.getQuantity()).isEqualTo(10);

        verify(hubAccessValidator)
                .validate(
                        hub,
                        requesterId,
                        requesterRole
                );

        verify(eventPublisher, never())
                .publishEvent(any(InventoryLowEvent.class));
    }

    @Test
    @DisplayName("존재하지 않는 허브 재고 수정 시 예외가 발생한다")
    void update_fail_notFound() {
        // given
        UUID inventoryId = UUID.randomUUID();
        UUID requesterId = UUID.randomUUID();
        UserRole requesterRole = UserRole.HUB_MANAGER;

        given(hubInventoryRepository.findById(inventoryId))
                .willReturn(Optional.empty());

        UpdateHubInventoryCommand command =
                UpdateHubInventoryCommand.builder()
                        .id(inventoryId)
                        .quantity(10)
                        .requesterId(requesterId)
                        .requesterRole(requesterRole)
                        .build();

        // when & then
        assertThatThrownBy(() ->
                updateHubInventoryService.update(command)
        )
                .isInstanceOf(ApiException.class)
                .hasMessage(
                        ErrorResponseCode.HUB_INVENTORY_NOT_FOUND.getMessage()
                );

        verify(hubInventoryRepository)
                .findById(inventoryId);

        verifyNoInteractions(hubAccessValidator);
        verifyNoInteractions(eventPublisher);
    }

    @Test
    @DisplayName("삭제된 허브 재고 수정 시 예외가 발생한다")
    void update_fail_alreadyDeleted() {
        // given
        UUID inventoryId = UUID.randomUUID();
        UUID requesterId = UUID.randomUUID();
        UserRole requesterRole = UserRole.HUB_MANAGER;

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
                        .requesterId(requesterId)
                        .requesterRole(requesterRole)
                        .build();

        // when & then
        assertThatThrownBy(() ->
                updateHubInventoryService.update(command)
        )
                .isInstanceOf(ApiException.class)
                .hasMessage(
                        ErrorResponseCode.HUB_INVENTORY_ALREADY_DELETED.getMessage()
                );

        assertThat(inventory.getQuantity()).isEqualTo(30);

        verify(hubInventoryRepository)
                .findById(inventoryId);

        verifyNoInteractions(hubAccessValidator);
        verifyNoInteractions(eventPublisher);
    }

    @Test
    @DisplayName("허브 재고 수량을 음수로 수정할 수 없다")
    void update_fail_negativeQuantity() {
        // given
        UUID inventoryId = UUID.randomUUID();
        UUID requesterId = UUID.randomUUID();
        UserRole requesterRole = UserRole.HUB_MANAGER;

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
                        .requesterId(requesterId)
                        .requesterRole(requesterRole)
                        .build();

        // when & then
        assertThatThrownBy(() ->
                updateHubInventoryService.update(command)
        )
                .isInstanceOf(ApiException.class)
                .hasMessage(
                        ErrorResponseCode.INVALID_STOCK_QUANTITY.getMessage()
                );

        assertThat(inventory.getQuantity()).isEqualTo(30);

        verify(hubAccessValidator)
                .validate(
                        hub,
                        requesterId,
                        requesterRole
                );

        verify(eventPublisher, never())
                .publishEvent(any(InventoryLowEvent.class));
    }
}