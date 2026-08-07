package com.sparta.logistics.domain.entity;

import com.sparta.logistics.common.code.ErrorResponseCode;
import com.sparta.logistics.common.exception.ApiException;
import com.sparta.logistics.infrastructure.persistence.jpa.entity.BaseUpdatableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Check;

import java.util.UUID;

@Entity
@Getter
@Table(
        name = "p_hub_inventories"
//        uniqueConstraints = {
//                @UniqueConstraint(columnNames = {"hub_id", "product_id"})
//        }
        //TODO:partial unique index
)
@Check(constraints = "quantity >= 0 AND safety_stock >= 0")
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

    private static final int DEFAULT_SAFETY_STOCK = 20;

    @Builder
    private HubInventory(
            UUID hubId,
            UUID productId,
            Integer quantity,
            Integer safetyStock
    ) {

        if(quantity == null || quantity < 0){
            throw new ApiException(ErrorResponseCode.INVALID_STOCK_QUANTITY);
        }

        if(safetyStock != null && safetyStock < 0){
            throw new ApiException(ErrorResponseCode.INVALID_SAFETY_STOCK);
        }

        this.hubId = hubId;
        this.productId = productId;
        this.quantity = quantity;
        this.safetyStock = safetyStock != null ? safetyStock : DEFAULT_SAFETY_STOCK;
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
