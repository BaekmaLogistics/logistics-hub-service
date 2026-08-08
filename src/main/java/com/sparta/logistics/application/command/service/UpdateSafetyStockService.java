package com.sparta.logistics.application.command.service;

import com.sparta.logistics.application.command.dto.hubinventory.UpdateSafetyStockCommand;
import com.sparta.logistics.application.command.dto.hubinventory.UpdateSafetyStockResponse;
import com.sparta.logistics.application.command.usecase.UpdateSafetyStockUseCase;
import com.sparta.logistics.common.code.ErrorResponseCode;
import com.sparta.logistics.common.exception.ApiException;
import com.sparta.logistics.domain.entity.HubInventory;
import com.sparta.logistics.domain.repository.HubInventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateSafetyStockService implements UpdateSafetyStockUseCase {

    private final HubInventoryRepository hubInventoryRepository;

    @Override
    @Transactional
    public UpdateSafetyStockResponse updateSafetyStock(UpdateSafetyStockCommand command){
        HubInventory inventory = hubInventoryRepository.findById(command.getInventoryId())
                .orElseThrow(() -> new ApiException(ErrorResponseCode.HUB_INVENTORY_NOT_FOUND));

        if(inventory.isDeleted()){
            throw new ApiException(ErrorResponseCode.HUB_INVENTORY_ALREADY_DELETED);
        }

        boolean wasLowStock = inventory.isLowStock();

        inventory.updateSafetyStock(command.getSafetyStock());

        boolean isLowStock = inventory.isLowStock();

        // TODO: InventoryLowEvent 발행
        // - quantity <= safetyStock 진입 시 발행
        // - quantity 변경뿐 아니라 safetyStock 변경으로 부족 상태가 된 경우도 포함
        // - 이미 부족 상태인 경우 중복 발행하지 않도록 이전/이후 상태 비교
        // - 트랜잭션 커밋과 이벤트 발행 시점 고려

        return UpdateSafetyStockResponse.from(inventory);
    }
}
