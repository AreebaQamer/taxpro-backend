package com.taxpro.controller;

import com.taxpro.entity.Post;
import com.taxpro.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@RequestMapping("/api/admin")
@CrossOrigin(origins = {"http://localhost:3000", "https://sqamer.com", "https://www.sqamer.com"}, allowCredentials = "true")
@Tag(name = "Admin Blog API", description = "Admin endpoints for blog posts")
public class PostController {

    @Autowired
    private PostService postService;

    @Operation(summary = "Get all posts (Admin)", description = "Returns all posts including drafts")
    @GetMapping("/posts")
    public ResponseEntity<Page<Post>> getAllPosts(
            @Parameter(description = "Page number") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Filter by status") @RequestParam(required = false) String status) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("postDate").descending());
        Page<Post> posts = postService.getAllPosts(status, pageable);
        return ResponseEntity.ok(posts);
    }

    @Operation(summary = "Get post by ID (Admin)")
    @GetMapping("/posts/{id}")
    public ResponseEntity<Post> getPostById(@PathVariable Long id) {
        return postService.getPostById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Create new post")
@PostMapping("/posts")
public ResponseEntity<Post> createPost(@RequestBody Post post) {
    try {
        System.out.println("========== CREATE POST CONTROLLER ==========");
        System.out.println("Received request to create post");
        System.out.println("Post Title: " + post.getPostTitle());
        System.out.println("Post Content: " + (post.getPostContent() != null ? post.getPostContent().substring(0, Math.min(50, post.getPostContent().length())) : "null"));
        
        Post savedPost = postService.createPost(post);
        
        System.out.println("Post created with ID: " + savedPost.getId());
        System.out.println("===========================================");
        
        return new ResponseEntity<>(savedPost, HttpStatus.CREATED);
        
    } catch (Exception e) {
        System.err.println("❌ Error in createPost controller: " + e.getMessage());
        e.printStackTrace();
        
        // Return detailed error
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", e.getMessage());
        errorResponse.put("type", e.getClass().getSimpleName());
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}

    @Operation(summary = "Update post")
    @PutMapping("/posts/{id}")
    public ResponseEntity<Post> updatePost(@PathVariable Long id, @RequestBody Post post) {
        try {
            Post updatedPost = postService.updatePost(id, post);
            return ResponseEntity.ok(updatedPost);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Delete post")
    @DeleteMapping("/posts/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
        try {
            postService.deletePost(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Publish post")
    @PatchMapping("/posts/{id}/publish")
    public ResponseEntity<Post> publishPost(@PathVariable Long id) {
        try {
            Post post = postService.publishPost(id);
            return ResponseEntity.ok(post);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Move to draft")
    @PatchMapping("/posts/{id}/draft")
    public ResponseEntity<Post> moveToDraft(@PathVariable Long id) {
        try {
            Post post = postService.moveToDraft(id);
            return ResponseEntity.ok(post);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
@GetMapping("/stats")
public ResponseEntity<Map<String, Long>> getStats() {
    Map<String, Long> stats = new HashMap<>();
    stats.put("total", postService.getTotalCount());
    stats.put("published", postService.getPublishedCount());
    stats.put("drafts", postService.getDraftCount());
    return ResponseEntity.ok(stats);
}
  // Add this to your PostController.java
@Operation(summary = "Search posts (Admin)")
@GetMapping("/posts/search")
public ResponseEntity<Page<Post>> searchPosts(
        @RequestParam String keyword,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(required = false) String status) {
    
    Pageable pageable = PageRequest.of(page, size, Sort.by("postDate").descending());
    Page<Post> posts = postService.searchPosts(keyword, status, pageable);
    return ResponseEntity.ok(posts);
}
}