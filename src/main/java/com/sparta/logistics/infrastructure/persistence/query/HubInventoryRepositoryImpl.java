package com.sparta.logistics.infrastructure.persistence.query;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.logistics.common.code.ErrorResponseCode;
import com.sparta.logistics.common.exception.ApiException;
import com.sparta.logistics.domain.entity.HubInventory;
import com.sparta.logistics.domain.entity.QHubInventory;
import com.sparta.logistics.domain.repository.HubInventoryRepositoryCustom;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class HubInventoryRepositoryImpl implements HubInventoryRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    private final QHubInventory hubInventory = QHubInventory.hubInventory;

    private OrderSpecifier<?>[] getOrderSpecifier(Pageable pageable){

        Sort sort = pageable.getSort().isSorted()
                ? pageable.getSort()
                : Sort.by(Sort.Order.desc("createdAt"));



        List<OrderSpecifier<?>> orderSpecifiers = sort.stream()
                .map(order -> {
                    boolean asc = order.isAscending();

                    return switch (order.getProperty()) {
                        case "createdAt" ->
                                asc ? hubInventory.createdAt.asc()
                                        : hubInventory.createdAt.desc();

                        case "quantity" ->
                                asc ? hubInventory.quantity.asc()
                                        : hubInventory.quantity.desc();

                        case "safetyStock" ->
                                asc ? hubInventory.safetyStock.asc()
                                        : hubInventory.safetyStock.desc();

                        default ->
                                throw new ApiException(
                                        ErrorResponseCode.INVALID_SORT_PROPERTY
                                );
                    };
                })
                .collect(Collectors.toList());

        orderSpecifiers.add(hubInventory.id.asc());

        return orderSpecifiers.toArray(OrderSpecifier<?>[]::new);
    }

    private BooleanExpression notDeleted(){
        return hubInventory.deletedAt.isNull();
    }

    private BooleanExpression hubIdEq(UUID hubId) {
        return hubId != null
                ? hubInventory.hub.id.eq(hubId)
                : null;
    }

    private BooleanExpression productIdEq(UUID productId) {
        return productId != null
                ? hubInventory.productId.eq(productId)
                : null;
    }

    @Override
    public Page<HubInventory> search(
            UUID hubId,
            UUID productId,
            Pageable pageable
    ) {
        List<HubInventory> content = queryFactory
                .selectFrom(hubInventory)
                .where(
                        notDeleted(),
                        hubIdEq(hubId),
                        productIdEq(productId)
                )
                .orderBy(getOrderSpecifier(pageable))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(hubInventory.count())
                .from(hubInventory)
                .where(
                        notDeleted(),
                        hubIdEq(hubId),
                        productIdEq(productId)
                );

        return PageableExecutionUtils.getPage(
                content,
                pageable,
                countQuery::fetchOne
        );
    }
}
