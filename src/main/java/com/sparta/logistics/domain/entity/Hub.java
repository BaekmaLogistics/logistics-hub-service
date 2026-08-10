package com.sparta.logistics.domain.entity;

import com.sparta.logistics.infrastructure.persistence.jpa.entity.BaseUpdatableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Getter
@Table(name = "p_hubs")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Hub extends BaseUpdatableEntity {

    // TODO: DB에 Partial Unique Index 적용 필요
    // UNIQUE(name) WHERE deleted_at IS NULL

    // TODO: DB에 Partial Unique Index 적용 필요
    // 한 명의 허브 관리자가 여러 활성 허브에 배정되는 것 방지
    // UNIQUE(manager_id) WHERE deleted_at IS NULL
    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    @Column(name = "manager_id")
    private UUID managerId;

    @Builder
    private Hub(
            String name,
            String address,
            Double latitude,
            Double longitude,
            UUID managerId
    ) {
        this.name = name;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.managerId = managerId;
    }

    public static Hub create(
            String name,
            String address,
            Double latitude,
            Double longitude
    ){
        return Hub.builder()
                .name(name)
                .address(address)
                .latitude(latitude)
                .longitude(longitude)
                .build();
    }

    public void updateName(
            String name
    ) {
        this.name = name;
    }

    public void updateAddress(
            String address,
            Double latitude,
            Double longitude
    ){
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public void assignManagerId(
            UUID managerId
    ){
        this.managerId = managerId;
    }

    public boolean isDeleted() {
        return getDeletedAt() != null;
    }
}
