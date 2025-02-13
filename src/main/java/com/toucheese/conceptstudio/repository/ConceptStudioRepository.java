package com.toucheese.conceptstudio.repository;

import com.toucheese.conceptstudio.entity.ConceptStudio;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ConceptStudioRepository extends JpaRepository<ConceptStudio, Long>, ConceptStudioRepositoryCustom {
}
