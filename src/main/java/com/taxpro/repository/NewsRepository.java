package com.taxpro.repository;

import com.taxpro.entity.News;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface NewsRepository extends JpaRepository<News, Long> {
    
    Page<News> findByStatus(String status, Pageable pageable);
    
    List<News> findTop5ByStatusOrderByViewsDescCreatedAtDesc(String status);
    
    long countByStatus(String status);
    
    @Query("SELECT n FROM News n WHERE n.status = 'published' AND (n.title LIKE %:keyword% OR n.content LIKE %:keyword%)")
    Page<News> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);
    
    Page<News> findAllByOrderByCreatedAtDesc(Pageable pageable);
}