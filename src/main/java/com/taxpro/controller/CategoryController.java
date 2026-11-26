package com.taxpro.controller;

import com.taxpro.entity.Category;
import com.taxpro.entity.Post;
import com.taxpro.repository.CategoryRepository;
import com.taxpro.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private PostRepository postRepository;

    @GetMapping("/slug/{slug}")
    public ResponseEntity<?> getCategoryBySlug(@PathVariable String slug) {
        Category category = categoryRepository.findBySlug(slug)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        return ResponseEntity.ok(category);
    }

       @GetMapping("/{slug}/posts")
    public ResponseEntity<Map<String, Object>> getPostsByCategory(
            @PathVariable String slug,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size) {

        // 1. Pehle check karein ke category exist karti hai ya nahi (404 error se bachne ke liye)
        Category category = categoryRepository.findBySlug(slug)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        // 2. Pagination setup karein
        Pageable pageable = PageRequest.of(page, size);
        
        // 3. 👇 Naya method use karein 👇
        Page<Post> postPage = postRepository.findPublishedPostsByCategorySlug(slug, pageable);

        // 4. Frontend (React) ko data wese format mein bhejein jese wo expect kar raha hai
        Map<String, Object> response = new HashMap<>();
        response.put("posts", postPage.getContent());
        response.put("totalItems", postPage.getTotalElements());
        response.put("totalPages", postPage.getTotalPages());

        return ResponseEntity.ok(response);
    }
}