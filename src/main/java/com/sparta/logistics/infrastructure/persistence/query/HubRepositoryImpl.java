package com.sparta.logistics.infrastructure.persistence.query;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.logistics.domain.entity.Hub;
import com.sparta.logistics.domain.repository.HubRepositoryCustom;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;

import static com.sparta.logistics.domain.entity.QHub.hub;


@Repository
@RequiredArgsConstructor
public class HubRepositoryImpl implements HubRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    private BooleanExpression nameContains(String name){
        return StringUtils.hasText(name)
                ? hub.name.containsIgnoreCase(name) : null;
    }

    private BooleanExpression addressContains(String address){
        return StringUtils.hasText(address)
                ? hub.address.containsIgnoreCase(address) : null;
    }

    @Override
    public Page<Hub> search(
            String name,
            String address,
            Pageable pageable
    ) {
        List<Hub> content = queryFactory
                .selectFrom(hub)
                .where(
                        nameContains(name),
                        addressContains(address)
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(hub.count())
                .from(hub)
                .where(
                        nameContains(name),
                        addressContains(address)
                );

        return PageableExecutionUtils.getPage(
                content,
                pageable,
                countQuery::fetchOne
        );
    }
}
