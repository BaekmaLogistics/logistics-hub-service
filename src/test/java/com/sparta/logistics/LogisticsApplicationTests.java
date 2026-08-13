package com.sparta.logistics;

import com.sparta.logistics.domain.repository.HubInventoryRepository;
import com.sparta.logistics.domain.repository.HubRepository;
import com.sparta.logistics.domain.repository.HubRouteRepository;
import com.sparta.logistics.support.IntegrationTestSupport;
import com.sparta.logistics.domain.repository.OutboxEventRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

class LogisticsApplicationTests extends IntegrationTestSupport {

    void contextLoads() {
    }

}
