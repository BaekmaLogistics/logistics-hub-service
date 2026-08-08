package com.sparta.logistics.application.command.service;

import com.sparta.logistics.common.exception.ApiException;
import com.sparta.logistics.domain.entity.Hub;
import com.sparta.logistics.domain.entity.HubInventory;
import com.sparta.logistics.domain.entity.HubInventoryOperation;
import com.sparta.logistics.domain.repository.HubInventoryOperationRepository;
import com.sparta.logistics.domain.repository.HubInventoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
class RestoreHubInventoryServiceTest {

    @Mock
    private HubInventoryRepository hubInventoryRepository;

    @Mock
    private HubInventoryOperationRepository hubInventoryOperationRepository;

    private RestoreHubInventoryService restoreHubInventoryService;

    private UUID orderId;
    private UUID hubId;
    private UUID productId;

    private Hub hub;
    private HubInventory inventory;

    @BeforeEach
    void setUp() {
        restoreHubInventoryService = new RestoreHubInventoryService(
                hubInventoryRepository,
                hubInventoryOperationRepository
        );

        orderId = UUID.randomUUID();
        hubId = UUID.randomUUID();
        productId = UUID.randomUUID();

        hub = mock(Hub.class);

        inventory = HubInventory.builder()
                .hub(hub)
                .productId(productId)
                .quantity(90)
                .safetyStock(20)
                .build();
    }

    @Test
    @DisplayName("차감 이력이 존재하면 차감했던 수량만큼 재고를 복구한다.")
    void restore_success() {
        // given
        HubInventoryOperation operation = HubInventoryOperation.builder()
                .orderId(orderId)
                .hub(hub)
                .productId(productId)
                .quantity(10)
                .build();

        when(hubInventoryOperationRepository
                .findByOrderIdAndHub_IdAndProductId(
                        orderId,
                        hubId,
                        productId
                ))
                .thenReturn(Optional.of(operation));

        when(hubInventoryRepository
                .findByHubIdAndProductIdAndDeletedAtIsNull(
                        hubId,
                        productId
                ))
                .thenReturn(Optional.of(inventory));

        // when
        restoreHubInventoryService.restore(
                orderId,
                hubId,
                productId
        );

        // then
        assertEquals(100, inventory.getQuantity());
        assertTrue(operation.isRestored());

        verify(hubInventoryOperationRepository)
                .findByOrderIdAndHub_IdAndProductId(
                        orderId,
                        hubId,
                        productId
                );

        verify(hubInventoryRepository)
                .findByHubIdAndProductIdAndDeletedAtIsNull(
                        hubId,
                        productId
                );
    }

    @Test
    @DisplayName("이미 복구된 차감 이력이면 재고를 다시 복구하지 않는다.")
    void restore_alreadyRestored() {
        // given
        HubInventoryOperation operation = HubInventoryOperation.builder()
                .orderId(orderId)
                .hub(hub)
                .productId(productId)
                .quantity(10)
                .build();

        // 이미 복구된 상태
        operation.restore();

        when(hubInventoryOperationRepository
                .findByOrderIdAndHub_IdAndProductId(
                        orderId,
                        hubId,
                        productId
                ))
                .thenReturn(Optional.of(operation));

        // when
        restoreHubInventoryService.restore(
                orderId,
                hubId,
                productId
        );

        // then
        assertEquals(90, inventory.getQuantity());

        verify(hubInventoryOperationRepository)
                .findByOrderIdAndHub_IdAndProductId(
                        orderId,
                        hubId,
                        productId
                );

        // 이미 복구됐으므로 재고 자체를 조회할 필요도 없음
        verifyNoInteractions(hubInventoryRepository);
    }

    @Test
    @DisplayName("복구할 재고 차감 이력이 없으면 예외가 발생한다.")
    void restore_operationNotFound() {
        // given
        when(hubInventoryOperationRepository
                .findByOrderIdAndHub_IdAndProductId(
                        orderId,
                        hubId,
                        productId
                ))
                .thenReturn(Optional.empty());

        // when & then
        assertThrows(
                ApiException.class,
                () -> restoreHubInventoryService.restore(
                        orderId,
                        hubId,
                        productId
                )
        );

        verifyNoInteractions(hubInventoryRepository);
    }

    @Test
    @DisplayName("차감 이력은 존재하지만 허브 재고가 없으면 예외가 발생한다.")
    void restore_inventoryNotFound() {
        // given
        HubInventoryOperation operation = HubInventoryOperation.builder()
                .orderId(orderId)
                .hub(hub)
                .productId(productId)
                .quantity(10)
                .build();

        when(hubInventoryOperationRepository
                .findByOrderIdAndHub_IdAndProductId(
                        orderId,
                        hubId,
                        productId
                ))
                .thenReturn(Optional.of(operation));

        when(hubInventoryRepository
                .findByHubIdAndProductIdAndDeletedAtIsNull(
                        hubId,
                        productId
                ))
                .thenReturn(Optional.empty());

        // when & then
        assertThrows(
                ApiException.class,
                () -> restoreHubInventoryService.restore(
                        orderId,
                        hubId,
                        productId
                )
        );

        assertFalse(operation.isRestored());
    }
}