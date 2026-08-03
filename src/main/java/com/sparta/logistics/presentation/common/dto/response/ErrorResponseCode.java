package com.sparta.logistics.presentation.common.dto.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorResponseCode implements ApiResponseCode {
    // Common
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR,"COMMON_0001", "알 수 없는 오류가 발생했습니다."),
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "COMMON_0002","유효하지 않은 요청입니다."),
    FEIGN_CLIENT_ERROR(HttpStatus.BAD_GATEWAY, "COMMON_0003", "Feign 통신 중 오류가 발생했습니다.");


    private final HttpStatus status;
    private final String errorCode;
    private final String message;
}
