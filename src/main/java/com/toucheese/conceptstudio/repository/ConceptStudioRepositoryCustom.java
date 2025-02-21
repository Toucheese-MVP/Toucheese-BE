package com.toucheese.conceptstudio.repository;

import com.toucheese.conceptstudio.entity.ConceptStudio;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ConceptStudioRepositoryCustom {
    Page<ConceptStudio> findByConceptId(Long conceptId, Pageable pageable);
}
