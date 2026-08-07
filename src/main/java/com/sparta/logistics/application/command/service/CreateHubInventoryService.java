package com.sparta.logistics.application.command.service;

import com.sparta.logistics.application.command.dto.hubinventory.CreateHubInventoryCommand;
import com.sparta.logistics.application.command.dto.hubinventory.CreateHubInventoryResponse;
import com.sparta.logistics.application.command.usecase.CreateHubInventoryUseCase;
import com.sparta.logistics.common.code.ErrorResponseCode;
import com.sparta.logistics.common.exception.ApiException;
import com.sparta.logistics.domain.entity.Hub;
import com.sparta.logistics.domain.entity.HubInventory;
import com.sparta.logistics.domain.repository.HubInventoryRepository;
import com.sparta.logistics.domain.repository.HubRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CreateHubInventoryService implements CreateHubInventoryUseCase {

    private final HubInventoryRepository hubInventoryRepository;
    private final HubRepository hubRepository;

    @Override
    @Transactional
    public CreateHubInventoryResponse create(CreateHubInventoryCommand command){
        //허브 존재 확인
        Hub hub = hubRepository.findById(command.getHubId())
                .orElseThrow(() -> new ApiException(ErrorResponseCode.HUB_NOT_FOUND));

        //삭제된 허브에는 재고 등록 불가
        if(hub.isDeleted()){
            throw new ApiException(ErrorResponseCode.HUB_ALREADY_DELETED);
        }

        // TODO: Product 서비스 Internal API 연동 후 productId 유효성 검증
        // 존재하지 않거나 삭제된 상품의 재고 등록 방지

        // 동일 허브 + 상품의 활성 재고 중복 확인
        if(hubInventoryRepository.existsByHubIdAndProductIdAndDeletedAtIsNull(command.getHubId(), command.getProductId())){
            throw new ApiException(ErrorResponseCode.HUB_INVENTORY_ALREADY_EXISTS);
        }

        HubInventory inventory = HubInventory.builder()
                .hubId(command.getHubId())
                .productId(command.getProductId())
                .quantity(command.getQuantity())
                .build();

        HubInventory savedInventory = hubInventoryRepository.save(inventory);

        return CreateHubInventoryResponse.from(savedInventory);
    }
}
