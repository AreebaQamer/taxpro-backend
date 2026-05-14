package com.taxpro.repository;

import com.taxpro.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    
    // ✅ Sirf published posts (public website ke liye)
    Page<Post> findByPostStatus(String postStatus, Pageable pageable);
    
    // ✅ Published posts with type filter (blog ya news)
    Page<Post> findByPostStatusAndPostType(String postStatus, String postType, Pageable pageable);
    
    // ✅ Admin: Sab posts with type and status filter
    @Query("SELECT p FROM Post p WHERE " +
           "(:postType IS NULL OR p.postType = :postType) AND " +
           "(:status IS NULL OR p.postStatus = :status)")
    Page<Post> findAllPosts(@Param("postType") String postType, 
                           @Param("status") String status, 
                           Pageable pageable);
    
    // ✅ Search posts with filters
    @Query("SELECT p FROM Post p WHERE " +
           "(:postType IS NULL OR p.postType = :postType) AND " +
           "(:status IS NULL OR p.postStatus = :status) AND " +
           "(LOWER(p.postTitle) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(p.postContent) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Post> searchPosts(@Param("keyword") String keyword, 
                          @Param("postType") String postType,
                          @Param("status") String status, 
                          Pageable pageable);
    
    // ✅ Count posts by status
    long countByPostStatus(String postStatus);
    
    // ✅ Get single published post by ID
    Optional<Post> findByIdAndPostStatus(Long id, String postStatus);
}