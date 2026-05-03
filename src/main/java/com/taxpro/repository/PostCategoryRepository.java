package com.taxpro.repository;

import com.taxpro.entity.PostCategory;
import com.taxpro.entity.PostCategoryId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Repository
public interface PostCategoryRepository extends JpaRepository<PostCategory, PostCategoryId> {
    
    List<PostCategory> findByCategoryId(Long categoryId);
    
    List<PostCategory> findByPostId(Long postId);
    
    @Query("SELECT pc.postId FROM PostCategory pc WHERE pc.categoryId = :categoryId")
    List<Long> findPostIdsByCategoryId(@Param("categoryId") Long categoryId);
    
    @Modifying
    @Transactional
    void deleteByPostIdAndCategoryId(Long postId, Long categoryId);
    
    @Modifying
    @Transactional
    void deleteByPostId(Long postId);
}