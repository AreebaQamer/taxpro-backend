package com.taxpro.controller;

import com.taxpro.entity.Post;
import com.taxpro.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")  // Base URL: /api/admin
@CrossOrigin(origins = {"http://localhost:3000", "https://sqamer.com"})
public class PostController {

    @Autowired
    private PostService postService;

    // ✅ GET /api/admin/posts - Sab posts (draft + published) with filters
    @GetMapping("/posts")
    public ResponseEntity<Page<Post>> getAllPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String type,    // blog ya news
            @RequestParam(required = false) String status) {  // draft ya publish
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("postDate").descending());
        Page<Post> posts = postService.getAllPosts(type, status, pageable);
        return ResponseEntity.ok(posts);
    }
    
    // ✅ GET /api/admin/posts/{id} - Single post (admin ke liye)
    @GetMapping("/posts/{id}")
    public ResponseEntity<Post> getPostById(@PathVariable Long id) {
        return postService.getPostById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    // ✅ POST /api/admin/posts - Create new post
    @PostMapping("/posts")
    public ResponseEntity<?> createPost(@RequestBody Post post) {
        try {
            System.out.println("=== CREATE POST API CALLED ===");
            System.out.println("Title: " + post.getPostTitle());
            System.out.println("Type: " + post.getPostType());  // blog ya news
            
            Post savedPost = postService.createPost(post);
            return new ResponseEntity<>(savedPost, HttpStatus.CREATED);
            
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    // ✅ PUT /api/admin/posts/{id} - Update post
    @PutMapping("/posts/{id}")
    public ResponseEntity<Post> updatePost(@PathVariable Long id, @RequestBody Post post) {
        try {
            Post updatedPost = postService.updatePost(id, post);
            return ResponseEntity.ok(updatedPost);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    // ✅ DELETE /api/admin/posts/{id} - Delete post
    @DeleteMapping("/posts/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
        try {
            postService.deletePost(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    // ✅ PATCH /api/admin/posts/{id}/publish - Publish post
    @PatchMapping("/posts/{id}/publish")
    public ResponseEntity<Post> publishPost(@PathVariable Long id) {
        try {
            Post post = postService.publishPost(id);
            return ResponseEntity.ok(post);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    // ✅ PATCH /api/admin/posts/{id}/draft - Move to draft
    @PatchMapping("/posts/{id}/draft")
    public ResponseEntity<Post> moveToDraft(@PathVariable Long id) {
        try {
            Post post = postService.moveToDraft(id);
            return ResponseEntity.ok(post);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    // ✅ GET /api/admin/stats - Get statistics
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Long>> getStats() {
        return ResponseEntity.ok(postService.getStats());
    }
    
    // ✅ GET /api/admin/posts/search - Search posts
    @GetMapping("/posts/search")
    public ResponseEntity<Page<Post>> searchPosts(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String status) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("postDate").descending());
        Page<Post> posts = postService.searchPosts(keyword, type, status, pageable);
        return ResponseEntity.ok(posts);
    }
}