package com.sparta.logistics.presentation.query.controller;

import com.sparta.logistics.application.query.dto.HubInventoryResponse;
import com.sparta.logistics.application.query.dto.HubInventorySearchCondition;
import com.sparta.logistics.application.query.usecase.HubInventoryQueryUseCase;
import com.sparta.logistics.domain.model.UserRole;
import com.sparta.logistics.infrastructure.security.CustomAccessDeniedHandler;
import com.sparta.logistics.infrastructure.security.CustomAuthenticationEntryPoint;
import com.sparta.logistics.infrastructure.security.GatewayHeaderAuthenticationFilter;
import com.sparta.logistics.infrastructure.security.SecurityConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Tag("unit")
@WebMvcTest(HubInventoryQueryController.class)
@Import({
        SecurityConfig.class,
        GatewayHeaderAuthenticationFilter.class,
        CustomAuthenticationEntryPoint.class,
        CustomAccessDeniedHandler.class
})
@TestPropertySource(properties = {
        "NAVER_MAP_URL=http://localhost"
})
class HubInventoryQueryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private HubInventoryQueryUseCase hubInventoryQueryUseCase;

    @Test
    @DisplayName("허브 재고 단건 조회 성공")
    void getHubInventory_success() throws Exception {
        // given
        UUID requesterId = UUID.randomUUID();
        UUID inventoryId = UUID.randomUUID();
        UUID hubId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();

        HubInventoryResponse response =
                HubInventoryResponse.builder()
                        .id(inventoryId)
                        .hubId(hubId)
                        .productId(productId)
                        .quantity(30)
                        .safetyStock(20)
                        .build();

        given(hubInventoryQueryUseCase.getHubInventory(
                inventoryId,
                requesterId,
                UserRole.HUB_MANAGER
        )).willReturn(response);

        // when & then
        mockMvc.perform(
                        get("/api/v1/hub-inventories/{inventoryId}", inventoryId)
                                .header("X-User-Id", requesterId.toString())
                                .header("X-User-Role", "HUB_MANAGER")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id")
                        .value(inventoryId.toString()))
                .andExpect(jsonPath("$.data.hubId")
                        .value(hubId.toString()))
                .andExpect(jsonPath("$.data.productId")
                        .value(productId.toString()))
                .andExpect(jsonPath("$.data.quantity")
                        .value(30))
                .andExpect(jsonPath("$.data.safetyStock")
                        .value(20));

        verify(hubInventoryQueryUseCase)
                .getHubInventory(
                        inventoryId,
                        requesterId,
                        UserRole.HUB_MANAGER
                );
    }

    @Test
    @DisplayName("허브 ID와 상품 ID로 허브 재고 목록 조회 성공")
    void searchHubInventories_success() throws Exception {
        // given
        UUID requesterId = UUID.randomUUID();
        UUID inventoryId = UUID.randomUUID();
        UUID hubId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();

        HubInventoryResponse response =
                HubInventoryResponse.builder()
                        .id(inventoryId)
                        .hubId(hubId)
                        .productId(productId)
                        .quantity(50)
                        .safetyStock(20)
                        .build();

        Page<HubInventoryResponse> page =
                new PageImpl<>(List.of(response));

        given(hubInventoryQueryUseCase.searchHubInventories(
                any(HubInventorySearchCondition.class),
                any(),
                eq(requesterId),
                eq(UserRole.MASTER)
        )).willReturn(page);

        // when & then
        mockMvc.perform(
                        get("/api/v1/hub-inventories")
                                .header("X-User-Id", requesterId.toString())
                                .header("X-User-Role", "MASTER")
                                .param("hubId", hubId.toString())
                                .param("productId", productId.toString())
                                .param("page", "0")
                                .param("size", "10")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content[0].id")
                        .value(inventoryId.toString()))
                .andExpect(jsonPath("$.data.content[0].hubId")
                        .value(hubId.toString()))
                .andExpect(jsonPath("$.data.content[0].productId")
                        .value(productId.toString()))
                .andExpect(jsonPath("$.data.content[0].quantity")
                        .value(50))
                .andExpect(jsonPath("$.data.content[0].safetyStock")
                        .value(20));

        verify(hubInventoryQueryUseCase)
                .searchHubInventories(
                        any(HubInventorySearchCondition.class),
                        any(),
                        eq(requesterId),
                        eq(UserRole.MASTER)
                );
    }
}