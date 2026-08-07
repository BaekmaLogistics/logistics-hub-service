package com.sparta.logistics.application.command.service;

import com.sparta.logistics.application.command.dto.hub.DeleteHubCommand;
import com.sparta.logistics.application.command.usecase.DeleteHubUseCase;
import com.sparta.logistics.common.code.ErrorResponseCode;
import com.sparta.logistics.common.exception.ApiException;
import com.sparta.logistics.domain.entity.Hub;
import com.sparta.logistics.domain.repository.HubRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DeleteHubService implements DeleteHubUseCase {

    private final HubRepository hubRepository;

    @Override
    @Transactional
    public void deleteHub(DeleteHubCommand command){
        Hub hub = hubRepository.findById(command.getId())
                .orElseThrow(() -> new ApiException(ErrorResponseCode.HUB_NOT_FOUND));

        if(hub.isDeleted()){
            throw new ApiException(ErrorResponseCode.HUB_ALREADY_DELETED);
        }

        // TODO: 허브 삭제 시 해당 허브와 연결된 HubRoute를 비활성화하고,
//      // 트랜잭션 커밋 후 HubGraph reload 및 최단 경로 캐시 무효화

        hub.softDelete(command.getDeletedBy());
    }
}
