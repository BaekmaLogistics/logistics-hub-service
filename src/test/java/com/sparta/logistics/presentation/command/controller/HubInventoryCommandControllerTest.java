package com.sparta.logistics.presentation.command.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.logistics.application.command.dto.hubinventory.CreateHubInventoryCommand;
import com.sparta.logistics.application.command.dto.hubinventory.CreateHubInventoryResponse;
import com.sparta.logistics.application.command.usecase.CreateHubInventoryUseCase;
import com.sparta.logistics.presentation.command.request.CreateHubInventoryRequest;
import com.sparta.logistics.presentation.common.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Tag("unit")
@WebMvcTest(HubInventoryCommandController.class)
@Import(GlobalExceptionHandler.class)
@TestPropertySource(properties = {
        "NAVER_MAP_URL=http://localhost"
})
class HubInventoryCommandControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CreateHubInventoryUseCase createHubInventoryUseCase;

    @Test
    @DisplayName("허브 재고 등록 성공")
    void createHubInventory_success() throws Exception {
        // given
        UUID inventoryId = UUID.randomUUID();
        UUID hubId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();

        CreateHubInventoryResponse response =
                CreateHubInventoryResponse.builder()
                        .id(inventoryId)
                        .hubId(hubId)
                        .productId(productId)
                        .quantity(100)
                        .safetyStock(20)
                        .build();

        given(createHubInventoryUseCase.create(
                any(CreateHubInventoryCommand.class)
        )).willReturn(response);

        String request = """
                {
                  "hubId": "%s",
                  "productId": "%s",
                  "quantity": 100
                }
                """.formatted(hubId, productId);

        // when & then
        mockMvc.perform(
                        post("/api/v1/hub-inventories")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(request)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.id")
                        .value(inventoryId.toString()))
                .andExpect(jsonPath("$.data.hubId")
                        .value(hubId.toString()))
                .andExpect(jsonPath("$.data.productId")
                        .value(productId.toString()))
                .andExpect(jsonPath("$.data.quantity")
                        .value(100))
                .andExpect(jsonPath("$.data.safetyStock")
                        .value(20));

        verify(createHubInventoryUseCase)
                .create(any(CreateHubInventoryCommand.class));
    }

    @Test
    @DisplayName("허브 재고 등록 시 수량이 음수이면 400을 반환한다")
    void createHubInventory_fail_negativeQuantity() throws Exception {
        UUID hubId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();

        String request = """
                {
                  "hubId": "%s",
                  "productId": "%s",
                  "quantity": -1
                }
                """.formatted(hubId, productId);

        mockMvc.perform(
                        post("/api/v1/hub-inventories")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(request)
                )
                .andExpect(status().isBadRequest());

        verifyNoInteractions(createHubInventoryUseCase);
    }
}