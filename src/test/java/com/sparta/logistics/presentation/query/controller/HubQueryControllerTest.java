package com.sparta.logistics.presentation.query.controller;

import com.sparta.logistics.application.query.dto.HubDetailResponse;
import com.sparta.logistics.application.query.dto.HubSearchCondition;
import com.sparta.logistics.application.query.usecase.HubQueryUseCase;
import com.sparta.logistics.common.code.ErrorResponseCode;
import com.sparta.logistics.common.exception.ApiException;
import com.sparta.logistics.domain.entity.Hub;
import com.sparta.logistics.infrastructure.security.CustomAccessDeniedHandler;
import com.sparta.logistics.infrastructure.security.CustomAuthenticationEntryPoint;
import com.sparta.logistics.infrastructure.security.GatewayHeaderAuthenticationFilter;
import com.sparta.logistics.infrastructure.security.SecurityConfig;
import com.sparta.logistics.presentation.common.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Tag("unit")
@WebMvcTest(HubQueryController.class)
@Import({
        SecurityConfig.class,
        GatewayHeaderAuthenticationFilter.class,
        CustomAuthenticationEntryPoint.class,
        CustomAccessDeniedHandler.class
})
@TestPropertySource(properties = {
        "NAVER_MAP_URL=http://localhost"
})
class HubQueryControllerTest {

    @MockitoBean
    private HubQueryUseCase hubQueryUseCase;

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("허브 단건 조회 성공")
    void getHub_success() throws Exception {
        // given
        UUID requesterId = UUID.randomUUID();
        UUID hubId = UUID.randomUUID();

        HubDetailResponse response = HubDetailResponse.from(
                Hub.create(
                        "서울 허브",
                        "서울특별시 송파구",
                        37.5145,
                        127.1059
                )
        );

        when(hubQueryUseCase.getHub(hubId))
                .thenReturn(response);

        // when & then
        mockMvc.perform(
                        get("/api/v1/hubs/{hubId}", hubId)
                                .header(
                                        "X-User-Id",
                                        requesterId.toString()
                                )
                                .header(
                                        "X-User-Role",
                                        "MASTER"
                                )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.message")
                                .value("요청이 성공적으로 처리되었습니다.")
                )
                .andExpect(
                        jsonPath("$.data.name")
                                .value("서울 허브")
                )
                .andExpect(
                        jsonPath("$.data.address")
                                .value("서울특별시 송파구")
                )
                .andExpect(
                        jsonPath("$.data.latitude")
                                .value(37.5145)
                )
                .andExpect(
                        jsonPath("$.data.longitude")
                                .value(127.1059)
                );

        verify(hubQueryUseCase).getHub(hubId);
    }

    @Test
    @DisplayName("존재하지 않는 허브 조회 시 404를 반환한다")
    void getHub_fail() throws Exception {
        // given
        UUID requesterId = UUID.randomUUID();
        UUID hubId = UUID.randomUUID();

        when(hubQueryUseCase.getHub(hubId))
                .thenThrow(
                        new ApiException(
                                ErrorResponseCode.HUB_NOT_FOUND
                        )
                );

        // when & then
        mockMvc.perform(
                        get("/api/v1/hubs/{hubId}", hubId)
                                .header(
                                        "X-User-Id",
                                        requesterId.toString()
                                )
                                .header(
                                        "X-User-Role",
                                        "MASTER"
                                )
                )
                .andExpect(status().isNotFound());

        verify(hubQueryUseCase).getHub(hubId);
    }

    @Test
    @DisplayName("허브 목록 조회 성공")
    void search_hubs_success() throws Exception {
        // given
        UUID requesterId = UUID.randomUUID();

        HubDetailResponse response = HubDetailResponse.from(
                Hub.create(
                        "서울 허브",
                        "서울특별시 송파구",
                        37.5145,
                        127.1059
                )
        );

        Page<HubDetailResponse> page =
                new PageImpl<>(List.of(response));

        when(hubQueryUseCase.searchHubs(
                any(HubSearchCondition.class),
                any(Pageable.class)
        )).thenReturn(page);

        // when & then
        mockMvc.perform(
                        get("/api/v1/hubs")
                                .header(
                                        "X-User-Id",
                                        requesterId.toString()
                                )
                                .header(
                                        "X-User-Role",
                                        "MASTER"
                                )
                                .param("name", "서울")
                                .param("page", "0")
                                .param("size", "10")
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.message")
                                .value("요청이 성공적으로 처리되었습니다.")
                )
                .andExpect(
                        jsonPath("$.data.content[0].name")
                                .value("서울 허브")
                );

        verify(hubQueryUseCase).searchHubs(
                any(HubSearchCondition.class),
                any(Pageable.class)
        );
    }

    @Test
    @DisplayName("조회 결과가 없으면 빈 목록을 반환한다")
    void search_hubs_empty() throws Exception {
        // given
        UUID requesterId = UUID.randomUUID();

        Page<HubDetailResponse> page = Page.empty();

        when(hubQueryUseCase.searchHubs(
                any(HubSearchCondition.class),
                any(Pageable.class)
        )).thenReturn(page);

        // when & then
        mockMvc.perform(
                        get("/api/v1/hubs")
                                .header(
                                        "X-User-Id",
                                        requesterId.toString()
                                )
                                .header(
                                        "X-User-Role",
                                        "MASTER"
                                )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.data.content").isArray()
                )
                .andExpect(
                        jsonPath("$.data.content").isEmpty()
                );

        verify(hubQueryUseCase).searchHubs(
                any(HubSearchCondition.class),
                any(Pageable.class)
        );
    }

    @Test
    @DisplayName("인증 정보 없이 허브를 조회하면 401을 반환한다")
    void getHub_fail_unauthorized() throws Exception {
        // given
        UUID hubId = UUID.randomUUID();

        // when & then
        mockMvc.perform(
                        get("/api/v1/hubs/{hubId}", hubId)
                )
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(hubQueryUseCase);
    }
}