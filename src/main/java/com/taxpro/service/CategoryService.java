package com.taxpro.service;

import com.taxpro.entity.Category;
import com.taxpro.entity.Post;
import com.taxpro.entity.PostCategory;
import com.taxpro.repository.CategoryRepository;
import com.taxpro.repository.PostCategoryRepository;
import com.taxpro.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryService {
    
    @Autowired
    private CategoryRepository categoryRepository;
    
    @Autowired
    private PostCategoryRepository postCategoryRepository;
    
    @Autowired
    private PostRepository postRepository;
    
    /**
     * Get all categories
     */
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }
    
    /**
     * Get category by ID
     */
    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + id));
    }
    
    /**
     * Get category by slug
     */
    public Category getCategoryBySlug(String slug) {
        return categoryRepository.findBySlug(slug)
                .orElseThrow(() -> new RuntimeException("Category not found with slug: " + slug));
    }
    
    /**
     * Get posts by category slug with pagination
     */
    public Page<Post> getPostsByCategorySlug(String slug, int page, int size) {
        Category category = categoryRepository.findBySlug(slug)
                .orElseThrow(() -> new RuntimeException("Category not found: " + slug));
        
        List<Long> postIds = postCategoryRepository.findPostIdsByCategoryId(category.getId());
        
        if (postIds.isEmpty()) {
            return Page.empty();
        }
        
        Pageable pageable = PageRequest.of(page, size);
        
        // Get only published posts
        List<Post> allPosts = postRepository.findAllById(postIds).stream()
                .filter(post -> "publish".equals(post.getPostStatus()))
                .filter(post -> "post".equals(post.getPostType()))
                .collect(Collectors.toList());
        
        // Manual pagination
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), allPosts.size());
        
        if (start >= allPosts.size()) {
            return Page.empty();
        }
        
        List<Post> paginatedPosts = allPosts.subList(start, end);
        
        // Set category name for each post (for frontend)
        paginatedPosts.forEach(post -> {
            List<Category> categories = getCategoriesByPostId(post.getId());
            if (!categories.isEmpty()) {
                post.setCategoryName(categories.get(0).getName());
            }
        });
        
        return new PageImpl<>(paginatedPosts, pageable, allPosts.size());
    }
    
    /**
     * Get posts by category ID with pagination
     */
    public Page<Post> getPostsByCategoryId(Long categoryId, int page, int size) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + categoryId));
        
        List<Long> postIds = postCategoryRepository.findPostIdsByCategoryId(categoryId);
        
        if (postIds.isEmpty()) {
            return Page.empty();
        }
        
        Pageable pageable = PageRequest.of(page, size);
        
        List<Post> allPosts = postRepository.findAllById(postIds).stream()
                .filter(post -> "publish".equals(post.getPostStatus()))
                .filter(post -> "post".equals(post.getPostType()))
                .collect(Collectors.toList());
        
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), allPosts.size());
        
        if (start >= allPosts.size()) {
            return Page.empty();
        }
        
        List<Post> paginatedPosts = allPosts.subList(start, end);
        
        paginatedPosts.forEach(post -> {
            List<Category> categories = getCategoriesByPostId(post.getId());
            if (!categories.isEmpty()) {
                post.setCategoryName(categories.get(0).getName());
            }
        });
        
        return new PageImpl<>(paginatedPosts, pageable, allPosts.size());
    }
    
    /**
     * Get all categories for a specific post
     */
    public List<Category> getCategoriesByPostId(Long postId) {
        List<PostCategory> postCategories = postCategoryRepository.findByPostId(postId);
        return postCategories.stream()
                .map(pc -> categoryRepository.findById(pc.getCategoryId()).orElse(null))
                .filter(cat -> cat != null)
                .collect(Collectors.toList());
    }
    
    /**
     * Get category IDs for a specific post
     */
    public List<Long> getCategoryIdsByPostId(Long postId) {
        List<PostCategory> postCategories = postCategoryRepository.findByPostId(postId);
        return postCategories.stream()
                .map(PostCategory::getCategoryId)
                .collect(Collectors.toList());
    }
    
    /**
     * Assign a post to a category
     */
    @Transactional
    public void assignPostToCategory(Long postId, Long categoryId) {
        // Check if post exists
        if (!postRepository.existsById(postId)) {
            throw new RuntimeException("Post not found with id: " + postId);
        }
        
        // Check if category exists
        if (!categoryRepository.existsById(categoryId)) {
            throw new RuntimeException("Category not found with id: " + categoryId);
        }
        
        // Check if already assigned
        List<PostCategory> existing = postCategoryRepository.findByPostId(postId);
        boolean alreadyAssigned = existing.stream()
                .anyMatch(pc -> pc.getCategoryId().equals(categoryId));
        
        if (!alreadyAssigned) {
            PostCategory postCategory = new PostCategory();
            postCategory.setPostId(postId);
            postCategory.setCategoryId(categoryId);
            postCategoryRepository.save(postCategory);
        }
    }
    
    /**
     * Assign multiple categories to a post
     */
    @Transactional
    public void assignCategoriesToPost(Long postId, List<Long> categoryIds) {
        // First remove existing assignments
        List<PostCategory> existing = postCategoryRepository.findByPostId(postId);
        for (PostCategory pc : existing) {
            postCategoryRepository.delete(pc);
        }
        
        // Add new assignments
        for (Long categoryId : categoryIds) {
            assignPostToCategory(postId, categoryId);
        }
    }
    
    /**
     * Remove a post from a category
     */
    @Transactional
    public void removePostFromCategory(Long postId, Long categoryId) {
        postCategoryRepository.deleteByPostIdAndCategoryId(postId, categoryId);
    }
    
    /**
     * Get post count for a category
     */
    public long getPostCountByCategoryId(Long categoryId) {
        List<Long> postIds = postCategoryRepository.findPostIdsByCategoryId(categoryId);
        return postIds.stream()
                .filter(id -> {
                    return postRepository.findById(id)
                            .map(post -> "publish".equals(post.getPostStatus()) && "post".equals(post.getPostType()))
                            .orElse(false);
                })
                .count();
    }
    
    /**
     * Get post count for all categories
     */
    public List<Object[]> getPostCountForAllCategories() {
        List<Category> allCategories = getAllCategories();
        return allCategories.stream()
                .map(cat -> new Object[]{cat.getId(), cat.getName(), getPostCountByCategoryId(cat.getId())})
                .collect(Collectors.toList());
    }
    
    /**
     * Create a new category
     */
    @Transactional
    public Category createCategory(Category category) {
        if (category.getSlug() == null || category.getSlug().isEmpty()) {
            category.setSlug(category.getName().toLowerCase().replace(" ", "-"));
        }
        return categoryRepository.save(category);
    }
    
    /**
     * Update an existing category
     */
    @Transactional
    public Category updateCategory(Long id, Category categoryDetails) {
        Category category = getCategoryById(id);
        category.setName(categoryDetails.getName());
        category.setSlug(categoryDetails.getSlug());
        category.setDescription(categoryDetails.getDescription());
        category.setIcon(categoryDetails.getIcon());
        category.setColor(categoryDetails.getColor());
        return categoryRepository.save(category);
    }
    
    /**
     * Delete a category
     */
    @Transactional
    public void deleteCategory(Long id) {
        Category category = getCategoryById(id);
        // This will cascade delete from post_categories due to foreign key constraint
        categoryRepository.delete(category);
    }
}