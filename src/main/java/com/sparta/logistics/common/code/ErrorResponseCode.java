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

    INVALID_STOCK_QUANTITY(HttpStatus.BAD_REQUEST, "HUB_0001", "수량은 0 이상이어야 합니다."),
    INSUFFICIENT_STOCK(HttpStatus.BAD_REQUEST, "HUB_0002", "재고가 부족합니다."),
    INVALID_SAFETY_STOCK(HttpStatus.BAD_REQUEST, "HUB_0003", "안전 재고는 0 이상이어야 합니다."),
    HUB_ALREADY_EXISTS(HttpStatus.CONFLICT, "HUB_0004", "이미 존재하는 허브입니다."),
    HUB_NOT_FOUND(HttpStatus.NOT_FOUND, "HUB_0005", "허브를 찾을 수 없습니다."),
    INVALID_ADDRESS(HttpStatus.BAD_REQUEST, "HUB_0006", "유효하지 않은 주소입니다."),
    HUB_ALREADY_DELETED(HttpStatus.BAD_REQUEST, "HUB_0007", "삭제된 허브입니다."),
    HUB_ROUTE_ALREADY_EXISTS(HttpStatus.CONFLICT, "HUB_0008", "이미 존재하는 허브 연결입니다."),
    HUB_ROUTE_NOT_FOUND(HttpStatus.NOT_FOUND, "HUB_0009", "허브 연결을 찾을 수 없습니다."),
    INVALID_HUB_ROUTE(HttpStatus.BAD_REQUEST, "HUB_0010", "출발 허브와 도착 허브가 같을 수 없습니다."),
    PATH_NOT_FOUND(HttpStatus.NOT_FOUND, "HUB_0011", "출발 허브와 도착 허브 사이의 경로를 찾을 수 없습니다."),
    DIRECTION_API_ERROR(HttpStatus.BAD_GATEWAY, "HUB_0012", "경로 탐색 서비스 호출에 실패했습니다."),
    HUB_MANAGER_ALREADY_ASSIGNED(HttpStatus.BAD_REQUEST, "HUB_0013", "이미 해당 허브의 담당자로 배정된 관리자입니다."),
    INVALID_SORT_PROPERTY(HttpStatus.BAD_REQUEST, "HUB_0014", "지원하지 않는 정렬 기준입니다."),
    HUB_ROUTE_ALREADY_DELETED(HttpStatus.BAD_REQUEST, "HUB_0015", "삭제된 허브 경로입니다."),
    INVALID_HUB_ROUTE_UPDATE(HttpStatus.BAD_REQUEST, "HUB_0016", "수정할 허브 경로 정보가 없습니다."),
    HUB_INVENTORY_ALREADY_EXISTS(HttpStatus.CONFLICT, "HUB_0017", "해당 허브에 이미 등록된 상품 재고입니다.");

    private final HttpStatus status;
    private final String errorCode;
    private final String message;
}
