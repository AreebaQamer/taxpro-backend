package com.taxpro.controller;

import com.taxpro.entity.Category;
import com.taxpro.entity.Post;
import com.taxpro.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@CrossOrigin(origins = {"http://localhost:3000", "https://sqamer.com", "https://www.sqamer.com"}, allowCredentials = "true")
@Tag(name = "Category API", description = "Endpoints for blog categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    // ==================== GET ENDPOINTS ====================

    @Operation(summary = "Get all categories", description = "Returns list of all categories")
    @GetMapping("/categories")
    public ResponseEntity<List<Category>> getAllCategories() {
        List<Category> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(categories);
    }

    @Operation(summary = "Get category by ID", description = "Returns a single category by its ID")
    @GetMapping("/categories/{id}")
    public ResponseEntity<Category> getCategoryById(
            @Parameter(description = "Category ID", example = "1")
            @PathVariable Long id) {
        try {
            Category category = categoryService.getCategoryById(id);
            return ResponseEntity.ok(category);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Get category by slug", description = "Returns a single category by its slug")
    @GetMapping("/categories/slug/{slug}")
    public ResponseEntity<Category> getCategoryBySlug(
            @Parameter(description = "Category slug", example = "tax-matters")
            @PathVariable String slug) {
        try {
            Category category = categoryService.getCategoryBySlug(slug);
            return ResponseEntity.ok(category);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Get posts by category slug", description = "Returns paginated posts for a specific category")
    @GetMapping("/categories/{slug}/posts")
    public ResponseEntity<Map<String, Object>> getPostsByCategory(
            @Parameter(description = "Category slug", example = "tax-matters")
            @PathVariable String slug,
            @Parameter(description = "Page number (0-indexed)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size", example = "10")
            @RequestParam(defaultValue = "10") int size) {

        Page<Post> posts = categoryService.getPostsByCategorySlug(slug, page, size);
        
        // Get category info
        Category category = categoryService.getCategoryBySlug(slug);

        Map<String, Object> response = new HashMap<>();
        response.put("posts", posts.getContent());
        response.put("category", category);
        response.put("currentPage", posts.getNumber());
        response.put("totalItems", posts.getTotalElements());
        response.put("totalPages", posts.getTotalPages());
        response.put("pageSize", posts.getSize());

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get posts by category ID", description = "Returns paginated posts for a specific category ID")
    @GetMapping("/categories/id/{categoryId}/posts")
    public ResponseEntity<Map<String, Object>> getPostsByCategoryId(
            @Parameter(description = "Category ID", example = "1")
            @PathVariable Long categoryId,
            @Parameter(description = "Page number (0-indexed)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size", example = "10")
            @RequestParam(defaultValue = "10") int size) {

        Page<Post> posts = categoryService.getPostsByCategoryId(categoryId, page, size);
        
        Category category = categoryService.getCategoryById(categoryId);

        Map<String, Object> response = new HashMap<>();
        response.put("posts", posts.getContent());
        response.put("category", category);
        response.put("currentPage", posts.getNumber());
        response.put("totalItems", posts.getTotalElements());
        response.put("totalPages", posts.getTotalPages());
        response.put("pageSize", posts.getSize());

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get categories for a specific post", description = "Returns all categories assigned to a post")
    @GetMapping("/posts/{postId}/categories")
    public ResponseEntity<List<Category>> getPostCategories(
            @Parameter(description = "Post ID", example = "2873")
            @PathVariable Long postId) {
        List<Category> categories = categoryService.getCategoriesByPostId(postId);
        return ResponseEntity.ok(categories);
    }

    @Operation(summary = "Get post count for all categories", description = "Returns post count for each category")
    @GetMapping("/categories/stats")
    public ResponseEntity<List<Object[]>> getCategoryStats() {
        List<Object[]> stats = categoryService.getPostCountForAllCategories();
        return ResponseEntity.ok(stats);
    }

    @Operation(summary = "Get post count for a specific category", description = "Returns number of posts in a category")
    @GetMapping("/categories/{id}/count")
    public ResponseEntity<Map<String, Object>> getCategoryPostCount(
            @Parameter(description = "Category ID", example = "1")
            @PathVariable Long id) {
        long count = categoryService.getPostCountByCategoryId(id);
        Category category = categoryService.getCategoryById(id);
        
        Map<String, Object> response = new HashMap<>();
        response.put("categoryId", id);
        response.put("categoryName", category.getName());
        response.put("postCount", count);
        
        return ResponseEntity.ok(response);
    }

    // ==================== POST ENDPOINTS (Admin) ====================

    @Operation(summary = "Create a new category", description = "Admin endpoint to create a new category")
    @PostMapping("/admin/categories")
    public ResponseEntity<Category> createCategory(@RequestBody Category category) {
        try {
            Category savedCategory = categoryService.createCategory(category);
            return new ResponseEntity<>(savedCategory, HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Assign post to category", description = "Admin endpoint to assign a post to a category")
    @PostMapping("/admin/posts/{postId}/categories/{categoryId}")
    public ResponseEntity<Map<String, String>> assignPostToCategory(
            @Parameter(description = "Post ID", example = "2873")
            @PathVariable Long postId,
            @Parameter(description = "Category ID", example = "1")
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

    @Operation(summary = "Assign multiple categories to a post", description = "Admin endpoint to assign multiple categories to a post")
    @PostMapping("/admin/posts/{postId}/categories")
    public ResponseEntity<Map<String, String>> assignCategoriesToPost(
            @Parameter(description = "Post ID", example = "2873")
            @PathVariable Long postId,
            @RequestBody List<Long> categoryIds) {
        try {
            categoryService.assignCategoriesToPost(postId, categoryIds);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Categories assigned to post successfully");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // ==================== PUT ENDPOINTS (Admin) ====================

    @Operation(summary = "Update category", description = "Admin endpoint to update an existing category")
    @PutMapping("/admin/categories/{id}")
    public ResponseEntity<Category> updateCategory(
            @Parameter(description = "Category ID", example = "1")
            @PathVariable Long id,
            @RequestBody Category category) {
        try {
            Category updatedCategory = categoryService.updateCategory(id, category);
            return ResponseEntity.ok(updatedCategory);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ==================== DELETE ENDPOINTS (Admin) ====================

    @Operation(summary = "Remove post from category", description = "Admin endpoint to remove a post from a category")
    @DeleteMapping("/admin/posts/{postId}/categories/{categoryId}")
    public ResponseEntity<Map<String, String>> removePostFromCategory(
            @Parameter(description = "Post ID", example = "2873")
            @PathVariable Long postId,
            @Parameter(description = "Category ID", example = "1")
            @PathVariable Long categoryId) {
        try {
            categoryService.removePostFromCategory(postId, categoryId);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Post removed from category successfully");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @Operation(summary = "Delete category", description = "Admin endpoint to delete a category")
    @DeleteMapping("/admin/categories/{id}")
    public ResponseEntity<Map<String, String>> deleteCategory(
            @Parameter(description = "Category ID", example = "1")
            @PathVariable Long id) {
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
    
}