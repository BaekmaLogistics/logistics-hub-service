package com.sparta.logistics.domain.entity;

import com.sparta.logistics.common.code.ErrorResponseCode;
import com.sparta.logistics.common.exception.ApiException;
import com.sparta.logistics.infrastructure.persistence.jpa.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Getter
@Table(
        name = "p_hub_inventory_operations",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_hub_inventory_operation_order_hub_product",
                        columnNames = {
                                "order_id",
                                "hub_id",
                                "product_id"
                        }
                )
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class HubInventoryOperation extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "order_id", nullable = false)
    private UUID orderId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hub_id", nullable = false)
    private Hub hub;

    @Column(name = "product_id", nullable = false)
    private UUID productId;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private boolean restored;

    @Builder
    private HubInventoryOperation(
            UUID orderId,
            Hub hub,
            UUID productId,
            Integer quantity
    ) {
        if (orderId == null || hub == null || productId == null) {
            throw new ApiException(ErrorResponseCode.INVALID_REQUEST);
        }

        if (quantity == null || quantity <= 0) {
            throw new ApiException(
                    ErrorResponseCode.INVALID_STOCK_QUANTITY
            );
        }

        this.orderId = orderId;
        this.hub = hub;
        this.productId = productId;
        this.quantity = quantity;
        this.restored = false;
    }

    public void restore() {
        if (restored) {
            return;
        }

        this.restored = true;
    }
}
