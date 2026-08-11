package com.sparta.logistics.application.command.service;

import com.sparta.logistics.application.command.dto.hubinventory.CreateHubInventoryCommand;
import com.sparta.logistics.application.command.dto.hubinventory.CreateHubInventoryResponse;
import com.sparta.logistics.application.command.usecase.CreateHubInventoryUseCase;
import com.sparta.logistics.application.common.validator.HubAccessValidator;
import com.sparta.logistics.application.event.InventoryLowEvent;
import com.sparta.logistics.application.port.ProductValidator;
import com.sparta.logistics.common.code.ErrorResponseCode;
import com.sparta.logistics.common.exception.ApiException;
import com.sparta.logistics.domain.entity.Hub;
import com.sparta.logistics.domain.entity.HubInventory;
import com.sparta.logistics.domain.repository.HubInventoryRepository;
import com.sparta.logistics.domain.repository.HubRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class CreateHubInventoryService implements CreateHubInventoryUseCase {

    private final HubRepository hubRepository;
    private final HubAccessValidator hubAccessValidator;
    private final ProductValidator productValidator;
    private final HubInventoryCreator hubInventoryCreator;

    @Override
    public CreateHubInventoryResponse create(CreateHubInventoryCommand command){

        if(command.getQuantity() < 0){
            throw new ApiException(ErrorResponseCode.INVALID_STOCK_QUANTITY);
        }
        //허브 존재 확인
        Hub hub = hubRepository.findById(command.getHubId())
                .orElseThrow(() -> new ApiException(ErrorResponseCode.HUB_NOT_FOUND));

        //삭제된 허브에는 재고 등록 불가
        if(hub.isDeleted()){
            throw new ApiException(ErrorResponseCode.HUB_ALREADY_DELETED);
        }

        hubAccessValidator.validate(
                hub,
                command.getRequesterId(),
                command.getRequesterRole()
        );

        productValidator.validateExists(command.getProductId());

        return hubInventoryCreator.create(hub, command);
    }
}
