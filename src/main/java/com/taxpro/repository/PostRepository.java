package com.taxpro.repository;

import com.taxpro.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import org.springframework.data.repository.query.Param;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    Page<Post> findByPostStatus(String postStatus, Pageable pageable);

    long countByPostStatus(String postStatus);

    @Query("SELECT p FROM Post p WHERE p.postStatus = 'publish' AND p.postType = 'post'")
    Page<Post> findAllPublishedPosts(Pageable pageable);

    // ✅ String ki jagah LocalDateTime parameter use karo
    @Query("SELECT p FROM Post p WHERE p.postStatus = 'publish' AND p.postType = 'post' AND p.postDate < :cutoff")
    Page<Post> findPublishedPostsBefore2026(
        @org.springframework.data.repository.query.Param("cutoff") java.time.LocalDateTime cutoff,
        Pageable pageable
    );

    @Query("SELECT p FROM Post p WHERE p.id = :id AND p.postStatus = 'publish'")
    Optional<Post> findPublishedPostById(
        @org.springframework.data.repository.query.Param("id") Long id
    );

@Query(value = "SELECT CONCAT('https://sqamer.com/wp-content/uploads/', pm.meta_value) " +
       "FROM wppw_postmeta pm " +
       "WHERE pm.post_id = :postId AND pm.meta_key = '_wp_attached_file' LIMIT 1",
       nativeQuery = true)
    String findThumbnailByPostId(@Param("postId") Long postId);
// Add to PostRepository.java
@Query("SELECT p FROM Post p WHERE " +
       "LOWER(p.postTitle) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
       "LOWER(p.postContent) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
       "LOWER(p.postExcerpt) LIKE LOWER(CONCAT('%', :keyword, '%'))")
Page<Post> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

@Query("SELECT p FROM Post p WHERE " +
       "p.postStatus = :status AND " +
       "(LOWER(p.postTitle) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
       "LOWER(p.postContent) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
       "LOWER(p.postExcerpt) LIKE LOWER(CONCAT('%', :keyword, '%')))")
Page<Post> searchByKeywordAndStatus(@Param("keyword") String keyword, 
                                     @Param("status") String status, 
                                     Pageable pageable);
}