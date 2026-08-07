package com.sparta.logistics.infrastructure.persistence.query;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.logistics.application.query.dto.HubRouteSearchCondition;
import com.sparta.logistics.common.code.ErrorResponseCode;
import com.sparta.logistics.common.exception.ApiException;
import com.sparta.logistics.domain.entity.Hub;
import com.sparta.logistics.domain.entity.HubRoute;
import com.sparta.logistics.domain.entity.QHub;
import com.sparta.logistics.domain.entity.QHubRoute;
import com.sparta.logistics.domain.repository.HubRouteRepositoryCustom;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class HubRouteRepositoryImpl implements HubRouteRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    private final QHubRoute hubRoute = QHubRoute.hubRoute;

    private final QHub fromHub = new QHub("fromHub");
    private final QHub toHub = new QHub("toHub");

    private OrderSpecifier<?> getOrderSpecifier(Pageable pageable) {

        Sort.Order order = pageable.getSort().stream()
                .findFirst()
                .orElse(Sort.Order.desc("createdAt"));

        boolean asc = order.isAscending();

        return switch (order.getProperty()) {

            case "createdAt" ->
                    asc ? hubRoute.createdAt.asc()
                            : hubRoute.createdAt.desc();

            case "distance" ->
                    asc ? hubRoute.distance.asc()
                            : hubRoute.distance.desc();

            case "duration" ->
                    asc ? hubRoute.duration.asc()
                            : hubRoute.duration.desc();

            default ->
                    throw new ApiException(ErrorResponseCode.INVALID_SORT_PROPERTY);
        };
    }

    private BooleanExpression notDeleted() {
        return hubRoute.deletedAt.isNull();
    }

    private BooleanExpression keywordContains(String keyword){
        if(!StringUtils.hasText(keyword)){
            return null;
        }

        return fromHub.name.containsIgnoreCase(keyword)
                .or(toHub.name.containsIgnoreCase(keyword));
    }

    private BooleanExpression fromHubIdEq(UUID fromHubId) {
        return fromHubId != null ? fromHub.id.eq(fromHubId) : null;
    }

    private BooleanExpression toHubIdEq(UUID toHubId){
        return toHubId != null ? toHub.id.eq(toHubId) : null;
    }

    @Override
    public Page<HubRoute> search(
            String keyword,
            UUID fromHubId,
            UUID toHubId,
            Pageable pageable
    ) {
        List<HubRoute> content = queryFactory
                .selectFrom(hubRoute)
                .join(hubRoute.fromHub, fromHub)
                .join(hubRoute.toHub, toHub)
                .where(
                        notDeleted(),
                        keywordContains(keyword),
                        fromHubIdEq(fromHubId),
                        toHubIdEq(toHubId)
                )
                .orderBy(getOrderSpecifier(pageable))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(hubRoute.count())
                .from(hubRoute)
                .join(hubRoute.fromHub, fromHub)
                .join(hubRoute.toHub, toHub)
                .where(
                        notDeleted(),
                        keywordContains(keyword),
                        fromHubIdEq(fromHubId),
                        toHubIdEq(toHubId)
                );

        return PageableExecutionUtils.getPage(
                content,
                pageable,
                countQuery::fetchOne
        );

    }

    @Override
    public List<HubRoute> findAllActiveRoutesByHub(Hub hub){
        return queryFactory
                .selectFrom(hubRoute)
                .where(
                        hubRoute.deletedAt.isNull(),
                        hubRoute.fromHub.eq(hub)
                                .or(hubRoute.toHub.eq(hub))
                )
                .fetch();
    }
}
