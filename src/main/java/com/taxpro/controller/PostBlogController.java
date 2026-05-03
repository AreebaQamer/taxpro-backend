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
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class PostBlogController {

    @Autowired
    private PostService postService;

    @GetMapping("/posts")
    public ResponseEntity<Page<Post>> getPublishedPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("postDate").descending());

        Page<Post> posts;
        if (status != null && !status.isEmpty()) {
            posts = postService.getPostsByStatus(status, pageable);
        } else {
            posts = postService.getPublishedPosts(pageable);
        }

        return ResponseEntity.ok(posts);
    }

    @GetMapping("/posts/{id}")
    public ResponseEntity<Post> getPublishedPostById(@PathVariable Long id) {
        return postService.getPostById(id)
                .filter(post -> "publish".equals(post.getPostStatus()))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}