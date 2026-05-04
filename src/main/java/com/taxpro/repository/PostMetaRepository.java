package com.taxpro.repository;

import com.taxpro.entity.PostMeta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

@Repository
public interface PostMetaRepository extends JpaRepository<PostMeta, Long> {
    
    Optional<PostMeta> findByPostIdAndMetaKey(Long postId, String metaKey);
    
    @Query("SELECT pm.metaValue FROM PostMeta pm WHERE pm.postId = :postId AND pm.metaKey = '_thumbnail_url'")
    String findThumbnailByPostId(@Param("postId") Long postId);
    
    @Query("SELECT pm FROM PostMeta pm WHERE pm.postId = :postId AND pm.metaKey = '_thumbnail_url'")
    Optional<PostMeta> findThumbnailMetaByPostId(@Param("postId") Long postId);
    
    // ✅ YEH METHOD SAHI KARO
    @Modifying
    @Transactional
    @Query("DELETE FROM PostMeta pm WHERE pm.postId = :postId AND pm.metaKey = :metaKey")
    void deleteByPostIdAndMetaKey(@Param("postId") Long postId, @Param("metaKey") String metaKey);
}