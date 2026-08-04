package com.sparta.logistics.domain.entity;

import com.sparta.logistics.infrastructure.persistence.jpa.entity.BaseUpdatableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Getter
@Table(
        name = "p_hub_routes",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"from_hub_id", "to_hub_id"})
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class HubRoute extends BaseUpdatableEntity {

    @Column(name = "from_hub_id", nullable = false)
    private UUID fromHubId;

    @Column(name = "to_hub_id", nullable = false)
    private UUID toHubId;

    @Column(nullable = false)
    private Double distance;

    @Column(nullable = false)
    private Integer duration;

    @Builder
    private HubRoute(
            UUID fromHubId,
            UUID toHubId,
            Double distance,
            Integer duration
    ) {
        this.fromHubId = fromHubId;
        this.toHubId = toHubId;
        this.distance = distance;
        this.duration = duration;
    }

    public void update(
            Double distance,
            Integer duration
    ) {
        this.distance = distance;
        this.duration = duration;
    }
}
