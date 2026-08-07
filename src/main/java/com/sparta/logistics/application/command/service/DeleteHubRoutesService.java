//Hub 삭제 과정에서 연결된 모든 Route 정리
package com.sparta.logistics.application.command.service;

import com.sparta.logistics.application.command.usecase.DeleteHubRoutesUseCase;
import com.sparta.logistics.domain.entity.Hub;
import com.sparta.logistics.domain.entity.HubRoute;
import com.sparta.logistics.domain.repository.HubRouteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeleteHubRoutesService implements DeleteHubRoutesUseCase {

    private final HubRouteRepository hubRouteRepository;

    @Override
    public void deleteRoutesByHub(
            Hub hub,
            UUID deletedBy
    ) {
        List<HubRoute> routes = hubRouteRepository.findAllActiveRoutesByHub(hub);

        routes.forEach(
                route -> route.softDelete(deletedBy)
        );
    }
}
