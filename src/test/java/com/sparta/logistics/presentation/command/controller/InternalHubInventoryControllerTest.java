package com.sparta.logistics.presentation.command.controller;

import com.sparta.logistics.application.command.usecase.DecreaseHubInventoryUseCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Tag("unit")
@WebMvcTest(InternalHubInventoryController.class)
@AutoConfigureMockMvc(addFilters = false)
@TestPropertySource(properties = {
        "NAVER_MAP_URL=http://localhost"
})
class InternalHubInventoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DecreaseHubInventoryUseCase decreaseHubInventoryUseCase;

    @Test
    @DisplayName("재고 차감 요청에 성공한다.")
    void decreaseInventory_success() throws Exception {
        // given
        UUID hubId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();

        String request = """
                {
                    "hubId": "%s",
                    "productId": "%s",
                    "quantity": 10
                }
                """.formatted(hubId, productId);

        // when & then
        mockMvc.perform(
                        patch("/internal/api/v1/hub-inventories/decrease")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(request)
                )
                .andExpect(status().isOk());

        verify(decreaseHubInventoryUseCase)
                .decrease(hubId, productId, 10);
    }

    @Test
    @DisplayName("재고 차감 수량이 0이면 400을 반환한다.")
    void decreaseInventory_zeroQuantity() throws Exception {
        // given
        UUID hubId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();

        String request = """
                {
                    "hubId": "%s",
                    "productId": "%s",
                    "quantity": 0
                }
                """.formatted(hubId, productId);

        // when & then
        mockMvc.perform(
                        patch("/internal/api/v1/hub-inventories/decrease")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(request)
                )
                .andExpect(status().isBadRequest());

        verifyNoInteractions(decreaseHubInventoryUseCase);
    }

    @Test
    @DisplayName("재고 차감 수량이 음수이면 400을 반환한다.")
    void decreaseInventory_negativeQuantity() throws Exception {
        // given
        UUID hubId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();

        String request = """
                {
                    "hubId": "%s",
                    "productId": "%s",
                    "quantity": -1
                }
                """.formatted(hubId, productId);

        // when & then
        mockMvc.perform(
                        patch("/internal/api/v1/hub-inventories/decrease")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(request)
                )
                .andExpect(status().isBadRequest());

        verifyNoInteractions(decreaseHubInventoryUseCase);
    }

    @Test
    @DisplayName("hubId가 없으면 재고 차감 요청에 400을 반환한다.")
    void decreaseInventory_missingHubId() throws Exception {
        // given
        UUID productId = UUID.randomUUID();

        String request = """
                {
                    "productId": "%s",
                    "quantity": 10
                }
                """.formatted(productId);

        // when & then
        mockMvc.perform(
                        patch("/internal/api/v1/hub-inventories/decrease")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(request)
                )
                .andExpect(status().isBadRequest());

        verifyNoInteractions(decreaseHubInventoryUseCase);
    }

    @Test
    @DisplayName("productId가 없으면 재고 차감 요청에 400을 반환한다.")
    void decreaseInventory_missingProductId() throws Exception {
        // given
        UUID hubId = UUID.randomUUID();

        String request = """
                {
                    "hubId": "%s",
                    "quantity": 10
                }
                """.formatted(hubId);

        // when & then
        mockMvc.perform(
                        patch("/internal/api/v1/hub-inventories/decrease")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(request)
                )
                .andExpect(status().isBadRequest());

        verifyNoInteractions(decreaseHubInventoryUseCase);
    }

    @Test
    @DisplayName("재고 차감 수량이 없으면 400을 반환한다.")
    void decreaseInventory_missingQuantity() throws Exception {
        // given
        UUID hubId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();

        String request = """
                {
                    "hubId": "%s",
                    "productId": "%s"
                }
                """.formatted(hubId, productId);

        // when & then
        mockMvc.perform(
                        patch("/internal/api/v1/hub-inventories/decrease")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(request)
                )
                .andExpect(status().isBadRequest());

        verifyNoInteractions(decreaseHubInventoryUseCase);
    }
}