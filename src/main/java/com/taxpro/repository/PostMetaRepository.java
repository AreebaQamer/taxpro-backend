package com.taxpro.repository;

import com.taxpro.entity.PostMeta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface PostMetaRepository extends JpaRepository<PostMeta, Long> {
   // PostMetaRepository.java
// PostMetaRepository.java — key match karo
@Query("SELECT pm FROM PostMeta pm WHERE pm.postId = :postId AND pm.metaKey = '_thumbnail_url'")
Optional<PostMeta> findThumbnailMeta(@Param("postId") Long postId);
}