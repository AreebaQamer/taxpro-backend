package com.taxpro.controller;

import com.taxpro.entity.Category;
import com.taxpro.entity.Post;
import com.taxpro.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = {"http://localhost:3000", "https://sqamer.com", "https://www.sqamer.com"})
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    // Get all categories
    @GetMapping("/categories")
    public ResponseEntity<List<Category>> getAllCategories() {
        return ResponseEntity.ok(categoryService.getAllCategories());
    }

    // Get category by ID
    @GetMapping("/categories/{id}")
    public ResponseEntity<Category> getCategoryById(@PathVariable Long id) {
        try {
            Category category = categoryService.getCategoryById(id);
            return ResponseEntity.ok(category);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Get posts by category slug
    @GetMapping("/categories/{slug}/posts")
    public ResponseEntity<Map<String, Object>> getPostsByCategory(
            @PathVariable String slug,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<Post> posts = categoryService.getPostsByCategorySlug(slug, page, size);
        
        Map<String, Object> response = new HashMap<>();
        response.put("posts", posts.getContent());
        response.put("currentPage", posts.getNumber());
        response.put("totalItems", posts.getTotalElements());
        response.put("totalPages", posts.getTotalPages());
        
        return ResponseEntity.ok(response);
    }

    // Get categories for a specific post
    @GetMapping("/posts/{postId}/categories")
    public ResponseEntity<List<Category>> getPostCategories(@PathVariable Long postId) {
        return ResponseEntity.ok(categoryService.getCategoriesByPostId(postId));
    }

    // Get post count for all categories
    @GetMapping("/categories/stats")
    public ResponseEntity<List<Object[]>> getCategoryStats() {
        return ResponseEntity.ok(categoryService.getPostCountForAllCategories());
    }

    // ==================== ADMIN ENDPOINTS ====================
    
    // Create new category
    @PostMapping("/admin/categories")
    public ResponseEntity<Category> createCategory(@RequestBody Category category) {
        try {
            Category savedCategory = categoryService.createCategory(category);
            return new ResponseEntity<>(savedCategory, HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Update category
    @PutMapping("/admin/categories/{id}")
    public ResponseEntity<Category> updateCategory(@PathVariable Long id, @RequestBody Category category) {
        try {
            Category updatedCategory = categoryService.updateCategory(id, category);
            return ResponseEntity.ok(updatedCategory);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Delete category
    @DeleteMapping("/admin/categories/{id}")
    public ResponseEntity<Map<String, String>> deleteCategory(@PathVariable Long id) {
        try {
            categoryService.deleteCategory(id);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Category deleted successfully");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // Assign post to category
    @PostMapping("/admin/posts/{postId}/categories/{categoryId}")
    public ResponseEntity<Map<String, String>> assignPostToCategory(
            @PathVariable Long postId,
            @PathVariable Long categoryId) {
        try {
            categoryService.assignPostToCategory(postId, categoryId);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Post assigned to category successfully");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}