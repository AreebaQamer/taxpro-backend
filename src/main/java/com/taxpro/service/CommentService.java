package com.taxpro.service;

import com.taxpro.entity.Comment;
import com.taxpro.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    // ── Public: user comment submit kare ─────────────────────────────────────
    public Comment submitComment(Long postId, String authorName,
                                  String authorEmail, String content,
                                  String authorIP) {
        Comment c = new Comment();
        c.setPostId(postId);
        c.setCommentAuthor(authorName.trim());
        c.setCommentAuthorEmail(authorEmail != null ? authorEmail.trim() : "");
        c.setCommentContent(content.trim());
        c.setCommentApproved("0");          // pending — admin approve karega
        c.setCommentType("comment");
        c.setCommentParent(0L);
        c.setUserId(0L);
        c.setCommentKarma(0);
        c.setCommentAuthorIP(authorIP != null ? authorIP : "");
         c.setCommentAuthorUrl("");                    // ✅ add karo
    c.setCommentAgent("Mozilla/5.0");             // ✅ yahi fix hai — null nahi ho sakta
        c.setCommentDate(LocalDateTime.now());
        c.setCommentDateGmt(LocalDateTime.now());
        return commentRepository.save(c);
    }

    // ── Public: approved comments fetch karo (comment_approved = '1') ────────
    public List<Comment> getApprovedComments(Long postId) {
        return commentRepository
            .findByPostIdAndCommentApprovedOrderByCommentDateAsc(postId, "1");
    }

    // ── Admin: sab comments ───────────────────────────────────────────────────
    public Page<Comment> getAllComments(Pageable pageable) {
        return commentRepository.findAllByOrderByCommentDateDesc(pageable);
    }

    // ── Admin: pending comments (comment_approved = '0') ──────────────────────
    public Page<Comment> getPendingComments(Pageable pageable) {
        return commentRepository
            .findByCommentApprovedOrderByCommentDateDesc("0", pageable);
    }

    // ── Admin: approve karo → comment_approved = '1' ──────────────────────────
    public Comment approveComment(Long id) {
        Comment c = commentRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Comment not found: " + id));
        c.setCommentApproved("1");
        return commentRepository.save(c);
    }

    // ── Admin: reject/spam karo → comment_approved = 'spam' ──────────────────
    public Comment rejectComment(Long id) {
        Comment c = commentRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Comment not found: " + id));
        c.setCommentApproved("spam");
        return commentRepository.save(c);
    }

    // ── Admin: delete karo ────────────────────────────────────────────────────
    public void deleteComment(Long id) {
        if (!commentRepository.existsById(id))
            throw new RuntimeException("Comment not found: " + id);
        commentRepository.deleteById(id);
    }

    // ── Admin badge: pending count ────────────────────────────────────────────
    public long getPendingCount() {
        return commentRepository.countByCommentApproved("0");
    }
}