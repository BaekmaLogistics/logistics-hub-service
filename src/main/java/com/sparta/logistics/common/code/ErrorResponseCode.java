package com.sparta.logistics.common.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorResponseCode implements ApiResponseCode {
    // Common
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR,"COMMON_0001", "알 수 없는 오류가 발생했습니다."),
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "COMMON_0002","유효하지 않은 요청입니다."),
    FEIGN_CLIENT_ERROR(HttpStatus.BAD_GATEWAY, "COMMON_0003", "Feign 통신 중 오류가 발생했습니다."),

    INVALID_STOCK_QUANTITY(HttpStatus.BAD_REQUEST, "HUB_0001", "수량은 0보다 커야 합니다."),
    INSUFFICIENT_STOCK(HttpStatus.BAD_REQUEST, "HUB_0002", "재고가 부족합니다."),
    INVALID_SAFETY_STOCK(HttpStatus.BAD_REQUEST, "HUB_0003", "안전 재고는 0 이상이어야 합니다."),
    HUB_ALREADY_EXISTS(HttpStatus.CONFLICT, "HUB_0004", "이미 존재하는 허브입니다."),
    HUB_NOT_FOUND(HttpStatus.NOT_FOUND, "HUB_0005", "허브를 찾을 수 없습니다."),
    INVALID_ADDRESS(HttpStatus.BAD_REQUEST, "HUB_0006", "유효하지 않은 주소입니다."),
    HUB_ALREADY_DELETED(HttpStatus.BAD_REQUEST, "HUB_0007", "삭제된 허브입니다.");


    private final HttpStatus status;
    private final String errorCode;
    private final String message;
}
