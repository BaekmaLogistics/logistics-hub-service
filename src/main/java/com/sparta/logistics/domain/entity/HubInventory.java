package com.sparta.logistics.domain.entity;

import com.sparta.logistics.infrastructure.persistence.jpa.entity.BaseUpdatableEntity;
import com.sparta.logistics.presentation.common.dto.response.ErrorResponseCode;
import com.sparta.logistics.presentation.common.exception.ApiException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Getter
@Table(
        name = "p_hub_inventories",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"hub_id", "product_id"})
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class HubInventory extends BaseUpdatableEntity {

    @Column(name = "hub_id", nullable = false)
    private UUID hubId;

    @Column(name = "product_id", nullable = false)
    private UUID productId;

    @PositiveOrZero
    @Column(nullable = false)
    private Integer quantity;

    @PositiveOrZero
    @Column(name = "safety_stock", nullable = false)
    private Integer safetyStock;

    @Builder
    private HubInventory(
            UUID hubId,
            UUID productId,
            Integer quantity,
            Integer safetyStock
    ) {
        this.hubId = hubId;
        this.productId = productId;
        this.quantity = quantity;
        this.safetyStock = safetyStock;
    }

    public void increaseQuantity(int quantity) {
        if(quantity <= 0) {
            throw new ApiException(ErrorResponseCode.INVALID_STOCK_QUANTITY);
        }

        this.quantity += quantity;
    }

    public void decreaseQuantity(int quantity) {
        if(quantity <= 0){
            throw new ApiException(ErrorResponseCode.INVALID_STOCK_QUANTITY);
        }

        if(this.quantity < quantity){
            throw new ApiException(ErrorResponseCode.INSUFFICIENT_STOCK);
        }

        this.quantity -= quantity;
    }

    public void updateSafetyStock(int safetyStock) {
        if(safetyStock < 0){
            throw new ApiException(ErrorResponseCode.INVALID_SAFETY_STOCK);
        }

        this.safetyStock = safetyStock;
    }
}
