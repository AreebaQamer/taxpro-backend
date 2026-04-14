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

    // Basic find methods
    Page<Post> findByPostStatus(String postStatus, Pageable pageable);
    
    // ✅ ADD THIS - Missing method for PostService line 22
    Page<Post> findByPostStatusAndPostType(String postStatus, String postType, Pageable pageable);
    
    // ✅ ADD THIS - Missing method for PostService line 23
    Page<Post> findByPostType(String postType, Pageable pageable);

    long countByPostStatus(String postStatus);
    
    long countByPostType(String postType);

    @Query("SELECT p FROM Post p WHERE p.postStatus = 'publish' AND p.postType = 'post'")
    Page<Post> findAllPublishedPosts(Pageable pageable);

    @Query("SELECT p FROM Post p WHERE p.postStatus = 'publish' AND p.postType = 'post' AND p.postDate < :cutoff")
    Page<Post> findPublishedPostsBefore2026(
        @Param("cutoff") java.time.LocalDateTime cutoff,
        Pageable pageable
    );

    @Query("SELECT p FROM Post p WHERE p.id = :id AND p.postStatus = 'publish'")
    Optional<Post> findPublishedPostById(@Param("id") Long id);

    @Query(value = "SELECT CONCAT('https://sqamer.com/wp-content/uploads/', pm.meta_value) " +
           "FROM wppw_postmeta pm " +
           "WHERE pm.post_id = :postId AND pm.meta_key = '_wp_attached_file' LIMIT 1",
           nativeQuery = true)
    String findThumbnailByPostId(@Param("postId") Long postId);

    // Search methods
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