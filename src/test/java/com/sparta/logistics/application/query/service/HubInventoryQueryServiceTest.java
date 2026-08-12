package com.sparta.logistics.application.query.service;

import com.sparta.logistics.application.common.validator.HubAccessValidator;
import com.sparta.logistics.application.query.dto.HubInventoryResponse;
import com.sparta.logistics.application.query.dto.HubInventorySearchCondition;
import com.sparta.logistics.common.code.ErrorResponseCode;
import com.sparta.logistics.common.exception.ApiException;
import com.sparta.logistics.domain.entity.Hub;
import com.sparta.logistics.domain.entity.HubInventory;
import com.sparta.logistics.domain.model.UserRole;
import com.sparta.logistics.domain.repository.HubInventoryRepository;
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
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
class HubInventoryQueryServiceTest {

    @Mock
    private HubInventoryRepository hubInventoryRepository;

    @Mock
    private HubAccessValidator hubAccessValidator;

    @Mock
    private HubRepository hubRepository;

    @InjectMocks
    private HubInventoryQueryService hubInventoryQueryService;

    @Test
    @DisplayName("허브 재고 단건 조회 성공")
    void getHubInventory_success() {
        // given
        UUID inventoryId = UUID.randomUUID();
        UUID hubId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        UUID requesterId = UUID.randomUUID();
        UserRole requesterRole = UserRole.HUB_MANAGER;

        Hub hub = createHub(hubId);

        HubInventory inventory = HubInventory.builder()
                .hub(hub)
                .productId(productId)
                .quantity(30)
                .build();

        ReflectionTestUtils.setField(inventory, "id", inventoryId);

        given(hubInventoryRepository.findById(inventoryId))
                .willReturn(Optional.of(inventory));

        // when
        HubInventoryResponse response =
                hubInventoryQueryService.getHubInventory(
                        inventoryId,
                        requesterId,
                        requesterRole
                );

        // then
        assertThat(response.getId()).isEqualTo(inventoryId);
        assertThat(response.getHubId()).isEqualTo(hubId);
        assertThat(response.getProductId()).isEqualTo(productId);
        assertThat(response.getQuantity()).isEqualTo(30);
        assertThat(response.getSafetyStock()).isEqualTo(20);

        verify(hubInventoryRepository)
                .findById(inventoryId);

        verify(hubAccessValidator)
                .validate(
                        hub,
                        requesterId,
                        requesterRole
                );
    }

    @Test
    @DisplayName("존재하지 않는 허브 재고 단건 조회 시 예외가 발생한다")
    void getHubInventory_fail_notFound() {
        // given
        UUID inventoryId = UUID.randomUUID();
        UUID requesterId = UUID.randomUUID();
        UserRole requesterRole = UserRole.HUB_MANAGER;

        given(hubInventoryRepository.findById(inventoryId))
                .willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() ->
                hubInventoryQueryService.getHubInventory(
                        inventoryId,
                        requesterId,
                        requesterRole
                )
        )
                .isInstanceOf(ApiException.class)
                .hasMessage(
                        ErrorResponseCode.HUB_INVENTORY_NOT_FOUND.getMessage()
                );

        verify(hubInventoryRepository)
                .findById(inventoryId);

        verifyNoInteractions(hubAccessValidator);
    }

    @Test
    @DisplayName("삭제된 허브 재고 단건 조회 시 조회할 수 없다")
    void getHubInventory_fail_deleted() {
        // given
        UUID inventoryId = UUID.randomUUID();
        UUID requesterId = UUID.randomUUID();
        UserRole requesterRole = UserRole.HUB_MANAGER;

        Hub hub = createHub(UUID.randomUUID());

        HubInventory inventory = HubInventory.builder()
                .hub(hub)
                .productId(UUID.randomUUID())
                .quantity(30)
                .build();

        ReflectionTestUtils.setField(inventory, "id", inventoryId);
        ReflectionTestUtils.setField(
                inventory,
                "deletedAt",
                Instant.now()
        );

        given(hubInventoryRepository.findById(inventoryId))
                .willReturn(Optional.of(inventory));

        // when & then
        assertThatThrownBy(() ->
                hubInventoryQueryService.getHubInventory(
                        inventoryId,
                        requesterId,
                        requesterRole
                )
        )
                .isInstanceOf(ApiException.class)
                .hasMessage(
                        ErrorResponseCode.HUB_INVENTORY_NOT_FOUND.getMessage()
                );

        verify(hubInventoryRepository)
                .findById(inventoryId);

        verifyNoInteractions(hubAccessValidator);
    }

    @Test
    @DisplayName("MASTER는 허브 ID와 상품 ID 조건으로 허브 재고 목록을 조회한다")
    void searchHubInventories_master_success() {
        // given
        UUID inventoryId = UUID.randomUUID();
        UUID hubId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        UUID requesterId = UUID.randomUUID();

        Hub hub = createHub(hubId);

        HubInventory inventory = HubInventory.builder()
                .hub(hub)
                .productId(productId)
                .quantity(50)
                .build();

        ReflectionTestUtils.setField(inventory, "id", inventoryId);

        Pageable pageable = PageRequest.of(0, 10);

        HubInventorySearchCondition condition =
                HubInventorySearchCondition.builder()
                        .hubId(hubId)
                        .productId(productId)
                        .build();

        Page<HubInventory> inventories =
                new PageImpl<>(
                        List.of(inventory),
                        pageable,
                        1
                );

        given(hubInventoryRepository.search(
                hubId,
                productId,
                pageable
        )).willReturn(inventories);

        // when
        Page<HubInventoryResponse> result =
                hubInventoryQueryService.searchHubInventories(
                        condition,
                        pageable,
                        requesterId,
                        UserRole.MASTER
                );

        // then
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent()).hasSize(1);

        HubInventoryResponse response =
                result.getContent().get(0);

        assertThat(response.getId()).isEqualTo(inventoryId);
        assertThat(response.getHubId()).isEqualTo(hubId);
        assertThat(response.getProductId()).isEqualTo(productId);
        assertThat(response.getQuantity()).isEqualTo(50);
        assertThat(response.getSafetyStock()).isEqualTo(20);

        verify(hubInventoryRepository)
                .search(hubId, productId, pageable);

        verifyNoInteractions(hubRepository);
    }

    @Test
    @DisplayName("HUB_MANAGER는 검색 조건이 없어도 담당 허브의 재고만 조회한다")
    void searchHubInventories_hubManager_success() {
        // given
        UUID requesterId = UUID.randomUUID();
        UUID managedHubId = UUID.randomUUID();
        UUID inventoryId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();

        Hub managedHub = createHub(managedHubId);

        HubInventory inventory = HubInventory.builder()
                .hub(managedHub)
                .productId(productId)
                .quantity(50)
                .build();

        ReflectionTestUtils.setField(inventory, "id", inventoryId);

        HubInventorySearchCondition condition =
                HubInventorySearchCondition.builder()
                        .build();

        Pageable pageable = PageRequest.of(0, 10);

        given(
                hubRepository.findByManagerIdAndDeletedAtIsNull(
                        requesterId
                )
        ).willReturn(Optional.of(managedHub));

        Page<HubInventory> inventories =
                new PageImpl<>(
                        List.of(inventory),
                        pageable,
                        1
                );

        given(hubInventoryRepository.search(
                managedHubId,
                null,
                pageable
        )).willReturn(inventories);

        // when
        Page<HubInventoryResponse> result =
                hubInventoryQueryService.searchHubInventories(
                        condition,
                        pageable,
                        requesterId,
                        UserRole.HUB_MANAGER
                );

        // then
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent()).hasSize(1);

        HubInventoryResponse response =
                result.getContent().get(0);

        assertThat(response.getHubId())
                .isEqualTo(managedHubId);

        assertThat(response.getProductId())
                .isEqualTo(productId);

        verify(hubRepository)
                .findByManagerIdAndDeletedAtIsNull(requesterId);

        verify(hubInventoryRepository)
                .search(
                        managedHubId,
                        null,
                        pageable
                );
    }

    @Test
    @DisplayName("HUB_MANAGER가 다른 허브의 재고를 조회하려 하면 예외가 발생한다")
    void searchHubInventories_hubManager_fail_otherHub() {
        // given
        UUID requesterId = UUID.randomUUID();
        UUID managedHubId = UUID.randomUUID();
        UUID otherHubId = UUID.randomUUID();

        Hub managedHub = createHub(managedHubId);

        HubInventorySearchCondition condition =
                HubInventorySearchCondition.builder()
                        .hubId(otherHubId)
                        .build();

        Pageable pageable = PageRequest.of(0, 10);

        given(
                hubRepository.findByManagerIdAndDeletedAtIsNull(
                        requesterId
                )
        ).willReturn(Optional.of(managedHub));

        // when & then
        assertThatThrownBy(() ->
                hubInventoryQueryService.searchHubInventories(
                        condition,
                        pageable,
                        requesterId,
                        UserRole.HUB_MANAGER
                )
        )
                .isInstanceOf(ApiException.class)
                .hasMessage(
                        ErrorResponseCode.HUB_ACCESS_DENIED.getMessage()
                );

        verify(hubRepository)
                .findByManagerIdAndDeletedAtIsNull(requesterId);

        verifyNoInteractions(hubInventoryRepository);
    }

    @Test
    @DisplayName("담당 허브가 없는 HUB_MANAGER가 재고 목록을 조회하면 예외가 발생한다")
    void searchHubInventories_hubManager_fail_noManagedHub() {
        // given
        UUID requesterId = UUID.randomUUID();

        HubInventorySearchCondition condition =
                HubInventorySearchCondition.builder()
                        .build();

        Pageable pageable = PageRequest.of(0, 10);

        given(
                hubRepository.findByManagerIdAndDeletedAtIsNull(
                        requesterId
                )
        ).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() ->
                hubInventoryQueryService.searchHubInventories(
                        condition,
                        pageable,
                        requesterId,
                        UserRole.HUB_MANAGER
                )
        )
                .isInstanceOf(ApiException.class)
                .hasMessage(
                        ErrorResponseCode.HUB_ACCESS_DENIED.getMessage()
                );

        verify(hubRepository)
                .findByManagerIdAndDeletedAtIsNull(requesterId);

        verifyNoInteractions(hubInventoryRepository);
    }

    private Hub createHub(UUID hubId) {
        Hub hub = Hub.builder()
                .name("서울 허브")
                .address("서울특별시")
                .latitude(37.1)
                .longitude(127.1)
                .build();

        ReflectionTestUtils.setField(hub, "id", hubId);

        return hub;
    }

    @Test
    @DisplayName("MASTER와 HUB_MANAGER가 아닌 사용자는 허브 재고 목록을 조회할 수 없다")
    void searchHubInventories_fail_accessDenied() {
        // given
        UUID requesterId = UUID.randomUUID();

        HubInventorySearchCondition condition =
                HubInventorySearchCondition.builder()
                        .hubId(UUID.randomUUID())
                        .productId(UUID.randomUUID())
                        .build();

        Pageable pageable = PageRequest.of(0, 10);

        // when & then
        assertThatThrownBy(() ->
                hubInventoryQueryService.searchHubInventories(
                        condition,
                        pageable,
                        requesterId,
                        UserRole.DELIVERY_MANAGER
                )
        )
                .isInstanceOf(ApiException.class)
                .hasMessage(
                        ErrorResponseCode.HUB_ACCESS_DENIED.getMessage()
                );

        verifyNoInteractions(hubInventoryRepository);
        verifyNoInteractions(hubRepository);
    }
}