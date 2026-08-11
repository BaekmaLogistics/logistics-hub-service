package com.sparta.logistics.application.command.service;

import com.sparta.logistics.application.command.dto.hubinventory.CreateHubInventoryCommand;
import com.sparta.logistics.application.command.dto.hubinventory.CreateHubInventoryResponse;
import com.sparta.logistics.application.common.validator.HubAccessValidator;
import com.sparta.logistics.application.port.ProductValidator;
import com.sparta.logistics.common.code.ErrorResponseCode;
import com.sparta.logistics.common.exception.ApiException;
import com.sparta.logistics.domain.entity.Hub;
import com.sparta.logistics.domain.model.UserRole;
import com.sparta.logistics.domain.repository.HubRepository;
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
import static org.mockito.Mockito.*;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
class CreateHubInventoryServiceTest {

    @Mock
    private HubRepository hubRepository;

    @Mock
    private HubAccessValidator hubAccessValidator;

    @Mock
    private ProductValidator productValidator;

    @Mock
    private HubInventoryCreator hubInventoryCreator;

    @InjectMocks
    private CreateHubInventoryService createHubInventoryService;

    @Test
    @DisplayName("허브 재고 등록 성공")
    void create_success() {
        // given
        UUID hubId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        UUID requesterId = UUID.randomUUID();
        UserRole requesterRole = UserRole.HUB_MANAGER;

        Hub hub = createHub(hubId);

        CreateHubInventoryCommand command =
                CreateHubInventoryCommand.builder()
                        .hubId(hubId)
                        .productId(productId)
                        .quantity(100)
                        .requesterId(requesterId)
                        .requesterRole(requesterRole)
                        .build();

        given(hubRepository.findById(hubId))
                .willReturn(Optional.of(hub));

        /*
         * HubInventoryCreator가 반환하는 DTO의 실제 생성 방식은
         * CreateHubInventoryResponse 구현에 따라 달라질 수 있으므로,
         * 여기서는 Mock 반환값 자체를 검증한다.
         */
        CreateHubInventoryResponse expectedResponse =
                mock(CreateHubInventoryResponse.class);

        given(hubInventoryCreator.create(hub, command))
                .willReturn(expectedResponse);

        // when
        CreateHubInventoryResponse response =
                createHubInventoryService.create(command);

        // then
        assertThat(response).isSameAs(expectedResponse);

        verify(hubRepository)
                .findById(hubId);

        verify(hubAccessValidator)
                .validate(hub, requesterId, requesterRole);

        verify(productValidator)
                .validateExists(productId);

        verify(hubInventoryCreator)
                .create(hub, command);
    }

    @Test
    @DisplayName("음수 수량으로 허브 재고를 등록할 수 없다")
    void create_fail_negativeQuantity() {
        // given
        CreateHubInventoryCommand command =
                CreateHubInventoryCommand.builder()
                        .hubId(UUID.randomUUID())
                        .productId(UUID.randomUUID())
                        .quantity(-1)
                        .requesterId(UUID.randomUUID())
                        .requesterRole(UserRole.HUB_MANAGER)
                        .build();

        // when & then
        assertThatThrownBy(() ->
                createHubInventoryService.create(command)
        )
                .isInstanceOf(ApiException.class)
                .hasMessage(
                        ErrorResponseCode.INVALID_STOCK_QUANTITY.getMessage()
                );

        /*
         * 로컬 입력값 검증에서 즉시 실패해야 한다.
         *
         * 특히 Product Feign 호출이 발생하지 않는 것을 검증하여
         * 잘못된 요청 때문에 불필요한 외부 서비스 호출이 발생하지 않도록 한다.
         */
        verifyNoInteractions(hubRepository);
        verifyNoInteractions(hubAccessValidator);
        verifyNoInteractions(productValidator);
        verifyNoInteractions(hubInventoryCreator);
    }

    @Test
    @DisplayName("존재하지 않는 허브에는 재고를 등록할 수 없다")
    void create_fail_hubNotFound() {
        // given
        UUID hubId = UUID.randomUUID();

        CreateHubInventoryCommand command =
                CreateHubInventoryCommand.builder()
                        .hubId(hubId)
                        .productId(UUID.randomUUID())
                        .quantity(100)
                        .requesterId(UUID.randomUUID())
                        .requesterRole(UserRole.HUB_MANAGER)
                        .build();

        given(hubRepository.findById(hubId))
                .willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() ->
                createHubInventoryService.create(command)
        )
                .isInstanceOf(ApiException.class)
                .hasMessage(
                        ErrorResponseCode.HUB_NOT_FOUND.getMessage()
                );

        verify(hubRepository)
                .findById(hubId);

        verifyNoInteractions(hubAccessValidator);
        verifyNoInteractions(productValidator);
        verifyNoInteractions(hubInventoryCreator);
    }

    @Test
    @DisplayName("삭제된 허브에는 재고를 등록할 수 없다")
    void create_fail_deletedHub() {
        // given
        UUID hubId = UUID.randomUUID();

        Hub hub = createHub(hubId);
        hub.softDelete(UUID.randomUUID());

        CreateHubInventoryCommand command =
                CreateHubInventoryCommand.builder()
                        .hubId(hubId)
                        .productId(UUID.randomUUID())
                        .quantity(100)
                        .requesterId(UUID.randomUUID())
                        .requesterRole(UserRole.HUB_MANAGER)
                        .build();

        given(hubRepository.findById(hubId))
                .willReturn(Optional.of(hub));

        // when & then
        assertThatThrownBy(() ->
                createHubInventoryService.create(command)
        )
                .isInstanceOf(ApiException.class)
                .hasMessage(
                        ErrorResponseCode.HUB_ALREADY_DELETED.getMessage()
                );

        verify(hubRepository)
                .findById(hubId);

        verifyNoInteractions(hubAccessValidator);
        verifyNoInteractions(productValidator);
        verifyNoInteractions(hubInventoryCreator);
    }

    @Test
    @DisplayName("존재하지 않는 상품은 허브 재고로 등록할 수 없다")
    void create_fail_productNotFound() {
        // given
        UUID hubId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        UUID requesterId = UUID.randomUUID();
        UserRole requesterRole = UserRole.HUB_MANAGER;

        Hub hub = createHub(hubId);

        CreateHubInventoryCommand command =
                CreateHubInventoryCommand.builder()
                        .hubId(hubId)
                        .productId(productId)
                        .quantity(100)
                        .requesterId(requesterId)
                        .requesterRole(requesterRole)
                        .build();

        given(hubRepository.findById(hubId))
                .willReturn(Optional.of(hub));

        doThrow(new RuntimeException("Product not found"))
                .when(productValidator)
                .validateExists(productId);

        // when & then
        assertThatThrownBy(() ->
                createHubInventoryService.create(command)
        )
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Product not found");

        verify(hubRepository)
                .findById(hubId);

        verify(hubAccessValidator)
                .validate(hub, requesterId, requesterRole);

        verify(productValidator)
                .validateExists(productId);

        /*
         * Product 검증에서 실패했으므로
         * 트랜잭션을 여는 HubInventoryCreator까지 진행하면 안 된다.
         */
        verifyNoInteractions(hubInventoryCreator);
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
}