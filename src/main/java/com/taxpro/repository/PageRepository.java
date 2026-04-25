// com/example/portal/repository/PageRepository.java
package com.taxpro.repository;

import com.taxpro.entity.PageContent;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PageRepository extends JpaRepository<PageContent, Long> {
    Optional<PageContent> findByPageId(String pageId);
}