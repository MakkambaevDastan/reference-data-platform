package com.platform.common.repository;

import com.platform.common.entity.Definition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface DefinitionRepository extends JpaRepository<Definition, Definition.Id>, JpaSpecificationExecutor<Definition> {
}
