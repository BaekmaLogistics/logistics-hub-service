package com.sparta.logistics.infrastructure.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.logistics.common.code.ErrorResponseCode;
import com.sparta.logistics.presentation.common.dto.response.ErrorResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException
    ) throws IOException, ServletException {

        ErrorResponseCode errorCode = ErrorResponseCode.UNAUTHORIZED;

        response.setStatus(errorCode.getStatus().value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        ErrorResponse errorResponse = new ErrorResponse(
                errorCode.getErrorCode(),
                errorCode.getMessage(),
                null
        );

        objectMapper.writeValue(
                response.getWriter(),
                errorResponse
        );
    }
}
