package com.platform.common.repository;

import com.platform.common.entity.ReferenceItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ReferenceItemRepository extends JpaRepository<ReferenceItem, Long>, JpaSpecificationExecutor<ReferenceItem> {
}
