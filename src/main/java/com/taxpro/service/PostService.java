package com.taxpro.service;

import com.taxpro.entity.Post;
import com.taxpro.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;
    
    @Autowired
    private PostMetaService postMetaService;

    // ✅ Helper: Generate URL slug from title
    private String generateSlug(String title) {
        if (title == null) return "untitled";
        return title.toLowerCase()
            .replaceAll("[^a-z0-9\\s-]", "")  // Remove special characters
            .replaceAll("\\s+", "-")           // Spaces to hyphens
            .replaceAll("-+", "-");            // Remove multiple hyphens
    }
    
    // ✅ ADMIN: Get all posts (with type and status filters)
    @Transactional
    public Page<Post> getAllPosts(String postType, String status, Pageable pageable) {
        Page<Post> posts = postRepository.findAllPosts(postType, status, pageable);
        
        // Add images to each post
        posts.forEach(post -> {
            String thumbnail = postMetaService.getThumbnail(post.getId());
            if (thumbnail != null && !thumbnail.isEmpty()) {
                post.setPostImage(thumbnail);
            }
        });
        
        return posts;
    }
    
    // ✅ PUBLIC: Get published posts (with type filter - blog ya news)
    public Page<Post> getPublishedPosts(String postType, Pageable pageable) {
        Page<Post> posts;
        
        if (postType != null && !postType.isEmpty()) {
            // Specific type (blog ya news)
            posts = postRepository.findByPostStatusAndPostType("publish", postType, pageable);
        } else {
            // All types
            posts = postRepository.findByPostStatus("publish", pageable);
        }
        
        // Add images to each post
        posts.forEach(post -> {
            String thumbnail = postMetaService.getThumbnail(post.getId());
            if (thumbnail != null && !thumbnail.isEmpty()) {
                post.setPostImage(thumbnail);
            }
        });
        
        return posts;
    }
    
    // ✅ ADMIN: Create new post
    @Transactional
    public Post createPost(Post post) {
        System.out.println("=== Creating New Post ===");
        System.out.println("Title: " + post.getPostTitle());
        System.out.println("Type: " + post.getPostType()); // blog ya news
        
        // Set default values
        LocalDateTime now = LocalDateTime.now();
        if (post.getPostDate() == null) {
            post.setPostDate(now);
            post.setPostDateGmt(now);
        }
        post.setPostModified(now);
        post.setPostModifiedGmt(now);
        
        if (post.getPostStatus() == null) {
            post.setPostStatus("draft");
        }
        
        // Default type: blog
        if (post.getPostType() == null || post.getPostType().isEmpty()) {
            post.setPostType("blog");
        }
        
        // Generate slug from title
        if (post.getPostName() == null || post.getPostName().isEmpty()) {
            post.setPostName(generateSlug(post.getPostTitle()));
        }
        
        // Save image separately (image base64 data)
        String imageData = post.getPostImage();
        post.setPostImage(null);  // Clear before saving to posts table
        
        // Save post
        Post saved = postRepository.save(post);
        System.out.println("✅ Post saved with ID: " + saved.getId());
        
        // Save thumbnail to postmeta table
        if (imageData != null && !imageData.isEmpty()) {
            postMetaService.saveThumbnail(saved.getId(), imageData);
            saved.setPostImage(imageData);  // Set back for response
            System.out.println("✅ Image saved");
        }
        
        return saved;
    }
    
    // ✅ ADMIN: Update existing post
    @Transactional
    public Post updatePost(Long id, Post updatedPost) {
        Post existing = postRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Post not found"));
        
        // Update fields
        existing.setPostTitle(updatedPost.getPostTitle());
        existing.setPostContent(updatedPost.getPostContent());
        existing.setPostExcerpt(updatedPost.getPostExcerpt());
        existing.setPostStatus(updatedPost.getPostStatus());
        existing.setPostType(updatedPost.getPostType());  // ✅ Blog ya News update
        existing.setPostModified(LocalDateTime.now());
        existing.setPostModifiedGmt(LocalDateTime.now());
        
        // Update slug if title changed
        if (!existing.getPostTitle().equals(updatedPost.getPostTitle())) {
            existing.setPostName(generateSlug(updatedPost.getPostTitle()));
        }
        
        Post saved = postRepository.save(existing);
        
        // Handle image update
        if (updatedPost.getPostImage() != null && !updatedPost.getPostImage().isEmpty()) {
            postMetaService.saveThumbnail(saved.getId(), updatedPost.getPostImage());
            saved.setPostImage(updatedPost.getPostImage());
        } else {
            // Keep existing image
            String existingImage = postMetaService.getThumbnail(saved.getId());
            saved.setPostImage(existingImage);
        }
        
        return saved;
    }
    
    // ✅ PUBLIC/ADMIN: Get single post by ID
    public Optional<Post> getPostById(Long id) {
        Optional<Post> post = postRepository.findById(id);
        post.ifPresent(p -> {
            String thumbnail = postMetaService.getThumbnail(p.getId());
            p.setPostImage(thumbnail);
        });
        return post;
    }
    
    // ✅ ADMIN: Delete post
    @Transactional
    public void deletePost(Long id) {
        if (!postRepository.existsById(id)) {
            throw new RuntimeException("Post not found");
        }
        // Delete image first
        postMetaService.deleteThumbnail(id);
        // Delete post
        postRepository.deleteById(id);
    }
    
    // ✅ ADMIN: Publish post (draft -> publish)
    @Transactional
    public Post publishPost(Long id) {
        Post post = postRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Post not found"));
        post.setPostStatus("publish");
        post.setPostModified(LocalDateTime.now());
        Post saved = postRepository.save(post);
        
        String thumbnail = postMetaService.getThumbnail(saved.getId());
        saved.setPostImage(thumbnail);
        return saved;
    }
    
    // ✅ ADMIN: Move to draft (publish -> draft)
    @Transactional
    public Post moveToDraft(Long id) {
        Post post = postRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Post not found"));
        post.setPostStatus("draft");
        post.setPostModified(LocalDateTime.now());
        Post saved = postRepository.save(post);
        
        String thumbnail = postMetaService.getThumbnail(saved.getId());
        saved.setPostImage(thumbnail);
        return saved;
    }
    
    // ✅ ADMIN: Get statistics (total, published, drafts)
    public Map<String, Long> getStats() {
        Map<String, Long> stats = new HashMap<>();
        stats.put("total", postRepository.count());
        stats.put("published", postRepository.countByPostStatus("publish"));
        stats.put("drafts", postRepository.countByPostStatus("draft"));
        return stats;
    }
    
    // ✅ ADMIN: Search posts
    public Page<Post> searchPosts(String keyword, String postType, String status, Pageable pageable) {
        Page<Post> posts = postRepository.searchPosts(keyword, postType, status, pageable);
        
        posts.forEach(post -> {
            String thumbnail = postMetaService.getThumbnail(post.getId());
            if (thumbnail != null && !thumbnail.isEmpty()) {
                post.setPostImage(thumbnail);
            }
        });
        
        return posts;
    }
}