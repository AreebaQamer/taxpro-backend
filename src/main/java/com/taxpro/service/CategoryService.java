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
    
    // Get all categories
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }
    
    // Get category by ID
    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + id));
    }
    
    // Get category by slug
    public Category getCategoryBySlug(String slug) {
        return categoryRepository.findBySlug(slug)
                .orElseThrow(() -> new RuntimeException("Category not found with slug: " + slug));
    }
    
    // Get posts by category slug with pagination
    public Page<Post> getPostsByCategorySlug(String slug, int page, int size) {
        Category category = categoryRepository.findBySlug(slug)
                .orElseThrow(() -> new RuntimeException("Category not found: " + slug));
        
        List<Long> postIds = postCategoryRepository.findPostIdsByCategoryId(category.getId());
        
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
        
        // Set category name for frontend
        paginatedPosts.forEach(post -> {
            List<Category> categories = getCategoriesByPostId(post.getId());
            if (!categories.isEmpty()) {
                post.setCategoryName(categories.get(0).getName());
            }
        });
        
        return new PageImpl<>(paginatedPosts, pageable, allPosts.size());
    }
    
    // Get categories for a specific post
    public List<Category> getCategoriesByPostId(Long postId) {
        List<PostCategory> postCategories = postCategoryRepository.findByPostId(postId);
        return postCategories.stream()
                .map(pc -> categoryRepository.findById(pc.getCategoryId()).orElse(null))
                .filter(cat -> cat != null)
                .collect(Collectors.toList());
    }
    
    // Assign post to category
    @Transactional
    public void assignPostToCategory(Long postId, Long categoryId) {
        if (!postRepository.existsById(postId)) {
            throw new RuntimeException("Post not found with id: " + postId);
        }
        
        if (!categoryRepository.existsById(categoryId)) {
            throw new RuntimeException("Category not found with id: " + categoryId);
        }
        
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
    
    // Remove post from category
    @Transactional
    public void removePostFromCategory(Long postId, Long categoryId) {
        postCategoryRepository.deleteByPostIdAndCategoryId(postId, categoryId);
    }
    
    // Get post count for all categories
    public List<Object[]> getPostCountForAllCategories() {
        List<Category> allCategories = getAllCategories();
        return allCategories.stream()
                .map(cat -> new Object[]{cat.getId(), cat.getName(), getPostCountByCategoryId(cat.getId())})
                .collect(Collectors.toList());
    }
    
    // Get post count for a specific category
    public long getPostCountByCategoryId(Long categoryId) {
        List<Long> postIds = postCategoryRepository.findPostIdsByCategoryId(categoryId);
        return postIds.stream()
                .filter(id -> postRepository.findById(id)
                        .map(post -> "publish".equals(post.getPostStatus()))
                        .orElse(false))
                .count();
    }
    
    // Create new category
    @Transactional
    public Category createCategory(Category category) {
        if (category.getSlug() == null || category.getSlug().isEmpty()) {
            category.setSlug(category.getName().toLowerCase().replace(" ", "-"));
        }
        return categoryRepository.save(category);
    }
    
    // Update category
    @Transactional
    public Category updateCategory(Long id, Category categoryDetails) {
        Category category = getCategoryById(id);
        category.setName(categoryDetails.getName());
        category.setSlug(categoryDetails.getSlug());
        category.setIcon(categoryDetails.getIcon());
        category.setColor(categoryDetails.getColor());
        return categoryRepository.save(category);
    }
    
    // Delete category
    @Transactional
    public void deleteCategory(Long id) {
        Category category = getCategoryById(id);
        categoryRepository.delete(category);
    }
}