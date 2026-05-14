package com.taxpro.controller;

import com.taxpro.entity.Post;
import com.taxpro.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")  // Base URL: /api
@CrossOrigin(origins = "*")  // Frontend ko access allow
public class PostBlogController {

    @Autowired
    private PostService postService;

    // ✅ GET /api/posts - Sab published posts (blog + news dono)
    @GetMapping("/posts")
    public ResponseEntity<Page<Post>> getAllPublishedPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("postDate").descending());
        Page<Post> posts = postService.getPublishedPosts(null, pageable);  // null = all types
        return ResponseEntity.ok(posts);
    }
    
    // ✅ GET /api/posts/blog - Sirf blog posts
    @GetMapping("/posts/blog")
    public ResponseEntity<Page<Post>> getBlogPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("postDate").descending());
        Page<Post> posts = postService.getPublishedPosts("blog", pageable);
        return ResponseEntity.ok(posts);
    }
    
    // ✅ GET /api/posts/news - Sirf news posts
    @GetMapping("/posts/news")
    public ResponseEntity<Page<Post>> getNewsPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("postDate").descending());
        Page<Post> posts = postService.getPublishedPosts("news", pageable);
        return ResponseEntity.ok(posts);
    }
    
    // ✅ GET /api/posts/{id} - Single post by ID
    @GetMapping("/posts/{id}")
    public ResponseEntity<Post> getPostById(@PathVariable Long id) {
        return postService.getPostById(id)
                .filter(post -> "publish".equals(post.getPostStatus()))  // Sirf published post
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}