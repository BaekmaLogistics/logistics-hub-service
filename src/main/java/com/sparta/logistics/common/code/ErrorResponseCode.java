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

    HUB_ALREADY_EXISTS(HttpStatus.CONFLICT, "HUB_0001", "이미 존재하는 허브입니다."),
    HUB_NOT_FOUND(HttpStatus.NOT_FOUND, "HUB_0002", "허브를 찾을 수 없습니다."),
    INVALID_ADDRESS(HttpStatus.BAD_REQUEST, "HUB_0003", "유효하지 않은 주소입니다."),
    HUB_ALREADY_DELETED(HttpStatus.BAD_REQUEST, "HUB_0004", "삭제된 허브입니다."),
    HUB_ROUTE_ALREADY_EXISTS(HttpStatus.CONFLICT, "HUB_0005", "이미 존재하는 허브 연결입니다."),
    HUB_MANAGER_ALREADY_ASSIGNED(HttpStatus.BAD_REQUEST, "HUB_0006", "이미 해당 허브의 담당자로 배정된 관리자입니다."),
    INVALID_SORT_PROPERTY(HttpStatus.BAD_REQUEST, "HUB_0007", "지원하지 않는 정렬 기준입니다."),

    HUB_ROUTE_NOT_FOUND(HttpStatus.NOT_FOUND, "HUB_ROUTE_0001", "허브 연결을 찾을 수 없습니다."),
    INVALID_HUB_ROUTE(HttpStatus.BAD_REQUEST, "HUB_ROUTE_0002", "출발 허브와 도착 허브가 같을 수 없습니다."),
    PATH_NOT_FOUND(HttpStatus.NOT_FOUND, "HUB_ROUTE_0003", "출발 허브와 도착 허브 사이의 경로를 찾을 수 없습니다."),
    DIRECTION_API_ERROR(HttpStatus.BAD_GATEWAY, "HUB_ROUTE_0004", "경로 탐색 서비스 호출에 실패했습니다."),
    HUB_ROUTE_ALREADY_DELETED(HttpStatus.BAD_REQUEST, "HUB_ROUTE_0005", "삭제된 허브 경로입니다."),
    INVALID_HUB_ROUTE_UPDATE(HttpStatus.BAD_REQUEST, "HUB_ROUTE_0006", "수정할 허브 경로 정보가 없습니다."),

    INVALID_STOCK_QUANTITY(HttpStatus.BAD_REQUEST, "HUB_INVENTORY_0001", "수량은 0 이상이어야 합니다."),
    INSUFFICIENT_STOCK(HttpStatus.BAD_REQUEST, "HUB_INVENTORY_0002", "재고가 부족합니다."),
    INVALID_SAFETY_STOCK(HttpStatus.BAD_REQUEST, "HUB_INVENTORY_0003", "안전 재고는 0 이상이어야 합니다."),
    HUB_INVENTORY_ALREADY_EXISTS(HttpStatus.CONFLICT, "HUB_INVENTORY_0004", "해당 허브에 이미 등록된 상품 재고입니다."),
    HUB_INVENTORY_NOT_FOUND(HttpStatus.NOT_FOUND, "HUB_INVENTORY_0005", "허브 재고를 찾을 수 없습니다."),
    HUB_INVENTORY_ALREADY_DELETED(HttpStatus.BAD_REQUEST, "HUB_INVENTORY_0006", "이미 삭제된 허브 재고입니다."),
    INVENTORY_LOCK_ACQUISITION_FAILED(HttpStatus.CONFLICT, "HUB_INVENTORY_0007","재고 처리 중입니다. 잠시 후 다시 시도해주세요."),
    INVENTORY_IDEMPOTENCY_CONFLICT(HttpStatus.CONFLICT, "HUB_INVENTORY_0008", "동일한 재고 차감 요청에 다른 수량이 요청되었습니다."),
    INVENTORY_OPERATION_NOT_FOUND(HttpStatus.NOT_FOUND, "HUB_INVENTORY_0009", "복구할 재고 차감 이력을 찾을 수 없습니다.");

    private final HttpStatus status;
    private final String errorCode;
    private final String message;
}
