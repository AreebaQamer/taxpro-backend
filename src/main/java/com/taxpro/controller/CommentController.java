package com.taxpro.controller;

import com.taxpro.entity.Comment;
import com.taxpro.service.CommentService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

// ══════════════════════════════════════════════════════════════════════════════
// PUBLIC — BlogDetail.jsx calls these
// ══════════════════════════════════════════════════════════════════════════════
@RestController
@RequestMapping("/api/posts/{postId}/comments")
@CrossOrigin(origins = "*")
class PublicCommentController {

    @Autowired
    private CommentService commentService;

    // GET /api/posts/5/comments — approved comments
    @GetMapping
    public ResponseEntity<List<Comment>> getComments(@PathVariable Long postId) {
        return ResponseEntity.ok(commentService.getApprovedComments(postId));
    }

    // POST /api/posts/5/comments — naya comment submit
    @PostMapping
    public ResponseEntity<?> submitComment(
            @PathVariable Long postId,
            @RequestBody Map<String, String> body,
            HttpServletRequest request) {

        String authorName  = body.get("authorName");
        String authorEmail = body.get("authorEmail");
        String content     = body.get("content");
        String ip          = request.getRemoteAddr();

        if (authorName == null || authorName.trim().isEmpty())
            return ResponseEntity.badRequest().body("Name required");
        if (content == null || content.trim().isEmpty())
            return ResponseEntity.badRequest().body("Comment required");

        Comment saved = commentService.submitComment(
                postId, authorName, authorEmail, content, ip);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }
}

// ══════════════════════════════════════════════════════════════════════════════
// ADMIN — admin panel calls these
// ══════════════════════════════════════════════════════════════════════════════
@RestController
@RequestMapping("/api/admin/comments")
@CrossOrigin(origins = {"http://localhost:3000", "https://sqamer.com", "https://www.sqamer.com"}, allowCredentials = "true")
class AdminCommentController {

    @Autowired
    private CommentService commentService;

    // GET /api/admin/comments — sab comments
    @GetMapping
    public ResponseEntity<Page<Comment>> getAllComments(
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable p = PageRequest.of(page, size);
        return ResponseEntity.ok(commentService.getAllComments(p));
    }

    // GET /api/admin/comments/pending — sirf pending
    @GetMapping("/pending")
    public ResponseEntity<Page<Comment>> getPending(
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable p = PageRequest.of(page, size);
        return ResponseEntity.ok(commentService.getPendingComments(p));
    }

    // GET /api/admin/comments/pending/count — badge ke liye
    @GetMapping("/pending/count")
    public ResponseEntity<Map<String, Long>> getPendingCount() {
        return ResponseEntity.ok(Map.of("count", commentService.getPendingCount()));
    }

    // PATCH /api/admin/comments/5/approve
    @PatchMapping("/{id}/approve")
    public ResponseEntity<Comment> approve(@PathVariable Long id) {
        try   { return ResponseEntity.ok(commentService.approveComment(id)); }
        catch (RuntimeException e) { return ResponseEntity.notFound().build(); }
    }

    // PATCH /api/admin/comments/5/reject
    @PatchMapping("/{id}/reject")
    public ResponseEntity<Comment> reject(@PathVariable Long id) {
        try   { return ResponseEntity.ok(commentService.rejectComment(id)); }
        catch (RuntimeException e) { return ResponseEntity.notFound().build(); }
    }

    // DELETE /api/admin/comments/5
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        try   { commentService.deleteComment(id); return ResponseEntity.noContent().build(); }
        catch (RuntimeException e) { return ResponseEntity.notFound().build(); }
    }
}