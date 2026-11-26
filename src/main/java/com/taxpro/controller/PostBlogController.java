package com.taxpro.controller;

import com.taxpro.entity.Category;
import com.taxpro.entity.Post;
import com.taxpro.repository.CategoryRepository;
import com.taxpro.repository.PostRepository; // 👈 Yeh add karein
import com.taxpro.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional; // 👈 Yeh add karein

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class PostBlogController {

    @Autowired
    private PostService postService;

    @Autowired
    private PostRepository postRepository; // 👈 Yeh inject karein

    @Autowired
    private CategoryRepository categoryRepository; // (Yeh pehle se tha, theek hai)

    @GetMapping("/posts")
    public ResponseEntity<Page<Post>> getPublishedPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("postDate").descending());

        // ✅ Sari published posts — koi date filter nahi
        Page<Post> posts = postService.getPublishedPosts(pageable);

        return ResponseEntity.ok(posts);
    }

    @GetMapping("/posts/{id}")
    public ResponseEntity<Post> getPublishedPostById(@PathVariable Long id) {
        return postService.getPostById(id)
                .filter(post -> "publish".equals(post.getPostStatus()))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 👇 Yeh method aapne add kiya tha, ab yeh kaam karega 👇
    @GetMapping("/categories/by-post-name")
    public ResponseEntity<?> getCategoryByPostName(@RequestParam String postTitle) {
        // Ab Optional import ho gaya hai aur postRepository bhi inject ho gaya hai
        Optional<Category> categoryOpt = postRepository.findCategoryByPostTitle(postTitle);
        
        if (categoryOpt.isPresent()) {
            return ResponseEntity.ok(categoryOpt.get());
        } else {
            return ResponseEntity.status(404).body("No category found for this post title");
        }
    }
}