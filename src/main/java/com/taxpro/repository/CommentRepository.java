package com.taxpro.repository;

import com.taxpro.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    // Frontend: post ke approved comments (comment_approved = '1')
    List<Comment> findByPostIdAndCommentApprovedOrderByCommentDateAsc(
            Long postId, String approved);

    // Admin: sab comments latest pehle
    Page<Comment> findAllByOrderByCommentDateDesc(Pageable pageable);

    // Admin: pending comments (comment_approved = '0')
    Page<Comment> findByCommentApprovedOrderByCommentDateDesc(
            String approved, Pageable pageable);

    // Admin badge: pending count
    long countByCommentApproved(String approved);
}