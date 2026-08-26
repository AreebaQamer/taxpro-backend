package com.taxpro.repository;

import com.taxpro.entity.AIContentCache;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface AIContentCacheRepository extends JpaRepository<AIContentCache, Integer> {
    Optional<AIContentCache> findByServiceKey(String serviceKey);
}