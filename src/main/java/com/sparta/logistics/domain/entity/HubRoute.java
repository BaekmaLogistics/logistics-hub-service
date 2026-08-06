package com.sparta.logistics.domain.entity;

import com.sparta.logistics.infrastructure.persistence.jpa.entity.BaseUpdatableEntity;
import jakarta.persistence.*;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_hub_id", nullable = false)
    private Hub fromHub;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_hub_id", nullable = false)
    private Hub toHub;

    @Column(nullable = false)
    private Double distance;

    @Column(nullable = false)
    private Integer duration;

    @Builder
    private HubRoute(
            Hub fromHub,
            Hub toHub,
            Double distance,
            Integer duration
    ) {
        this.fromHub = fromHub;
        this.toHub = toHub;
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
