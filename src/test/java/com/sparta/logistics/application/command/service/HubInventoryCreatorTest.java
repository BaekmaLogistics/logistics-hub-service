package com.sparta.logistics.application.command.service;

import com.sparta.logistics.application.command.dto.hubinventory.CreateHubInventoryCommand;
import com.sparta.logistics.application.command.dto.hubinventory.CreateHubInventoryResponse;
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

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
class HubInventoryCreatorTest {

    @Mock
    private HubInventoryRepository hubInventoryRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private HubInventoryCreator hubInventoryCreator;

    @Test
    @DisplayName("허브 재고 생성 성공")
    void create_success() {
        // given
        UUID hubId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();

        Hub hub = createHub(hubId);

        CreateHubInventoryCommand command =
                createCommand(hubId, productId, 100);

        given(
                hubInventoryRepository
                        .existsByHubAndProductIdAndDeletedAtIsNull(
                                hub,
                                productId
                        )
        ).willReturn(false);

        given(hubInventoryRepository.save(any(HubInventory.class)))
                .willAnswer(invocation -> {
                    HubInventory inventory = invocation.getArgument(0);
                    ReflectionTestUtils.setField(
                            inventory,
                            "id",
                            UUID.randomUUID()
                    );
                    return inventory;
                });

        // when
        CreateHubInventoryResponse response =
                hubInventoryCreator.create(hub, command);

        // then
        assertThat(response.getHubId()).isEqualTo(hubId);
        assertThat(response.getProductId()).isEqualTo(productId);
        assertThat(response.getQuantity()).isEqualTo(100);
        assertThat(response.getSafetyStock()).isEqualTo(20);

        verify(hubInventoryRepository)
                .existsByHubAndProductIdAndDeletedAtIsNull(
                        hub,
                        productId
                );

        verify(hubInventoryRepository)
                .save(any(HubInventory.class));

        // quantity 100 > safetyStock 20이므로 이벤트 발행 안 함
        verifyNoInteractions(eventPublisher);
    }

    @Test
    @DisplayName("동일 허브와 상품의 활성 재고가 존재하면 중복 등록할 수 없다")
    void create_fail_alreadyExists() {
        // given
        UUID hubId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();

        Hub hub = createHub(hubId);

        CreateHubInventoryCommand command =
                createCommand(hubId, productId, 100);

        given(
                hubInventoryRepository
                        .existsByHubAndProductIdAndDeletedAtIsNull(
                                hub,
                                productId
                        )
        ).willReturn(true);

        // when & then
        assertThatThrownBy(() ->
                hubInventoryCreator.create(hub, command)
        )
                .isInstanceOf(ApiException.class)
                .hasMessage(
                        ErrorResponseCode
                                .HUB_INVENTORY_ALREADY_EXISTS
                                .getMessage()
                );

        verify(hubInventoryRepository)
                .existsByHubAndProductIdAndDeletedAtIsNull(
                        hub,
                        productId
                );

        verify(hubInventoryRepository, never())
                .save(any(HubInventory.class));

        verifyNoInteractions(eventPublisher);
    }

    @Test
    @DisplayName("안전재고 이하로 재고가 생성되면 재고 부족 이벤트를 발행한다")
    void create_publishInventoryLowEvent_whenLowStock() {
        // given
        UUID hubId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        UUID inventoryId = UUID.randomUUID();

        Hub hub = createHub(hubId);

        // 기본 safetyStock = 20이므로 10으로 생성
        CreateHubInventoryCommand command =
                createCommand(hubId, productId, 10);

        given(
                hubInventoryRepository
                        .existsByHubAndProductIdAndDeletedAtIsNull(
                                hub,
                                productId
                        )
        ).willReturn(false);

        given(hubInventoryRepository.save(any(HubInventory.class)))
                .willAnswer(invocation -> {
                    HubInventory inventory = invocation.getArgument(0);

                    ReflectionTestUtils.setField(
                            inventory,
                            "id",
                            inventoryId
                    );

                    return inventory;
                });

        // when
        hubInventoryCreator.create(hub, command);

        // then
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

        verify(hubInventoryRepository)
                .save(any(HubInventory.class));
    }

    private Hub createHub(UUID hubId) {
        Hub hub = Hub.builder()
                .name("서울 허브")
                .address("서울")
                .latitude(37.1)
                .longitude(127.1)
                .build();

        ReflectionTestUtils.setField(hub, "id", hubId);

        return hub;
    }

    private CreateHubInventoryCommand createCommand(
            UUID hubId,
            UUID productId,
            int quantity
    ) {
        return CreateHubInventoryCommand.builder()
                .hubId(hubId)
                .productId(productId)
                .quantity(quantity)
                .requesterId(UUID.randomUUID())
                .requesterRole(UserRole.HUB_MANAGER)
                .build();
    }
}