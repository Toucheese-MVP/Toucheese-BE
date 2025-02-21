package com.toucheese.conceptstudio.repository;

import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.toucheese.conceptstudio.entity.ConceptStudio;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;

import static com.toucheese.conceptstudio.entity.QConceptStudio.conceptStudio;
import static com.toucheese.studio.entity.QStudio.studio;

@RequiredArgsConstructor
public class ConceptStudioRepositoryImpl implements ConceptStudioRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public Page<ConceptStudio> findByConceptId(Long conceptId, Pageable pageable) {
        List<ConceptStudio> content = queryFactory
                .select(conceptStudio)
                .from(conceptStudio)
                .leftJoin(conceptStudio.studio, studio).fetchJoin()
                .where(conceptStudio.concept.id.eq(conceptId))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(conceptStudio.count())
                .from(conceptStudio)
                .where(conceptStudio.concept.id.eq(conceptId));

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }
}
