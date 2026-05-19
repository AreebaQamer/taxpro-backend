package com.taxpro.service;

import com.taxpro.entity.Post;
import com.taxpro.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;
    
    @Autowired
    private PostMetaService postMetaService;

    @Transactional
    public Page<Post> getAllPosts(String status, Pageable pageable) {
        Page<Post> posts = status != null && !status.isEmpty()
            ? postRepository.findByPostStatusAndPostType(status, "post", pageable)
            : postRepository.findByPostType("post", pageable);

        // Fetch thumbnails for each post
        posts.forEach(post -> {
            String thumbnail = postMetaService.getThumbnail(post.getId());
            if (thumbnail != null && !thumbnail.isEmpty()) {
                post.setPostImage(thumbnail);
            }
        });

        return posts;
    }
@Transactional
public Post createPost(Post post) {
    System.out.println("=== CREATE POST WITH IMAGE ===");
    System.out.println("Title: " + post.getPostTitle());
    
    // Set default values
    if (post.getPostDate() == null) {
        post.setPostDate(LocalDateTime.now());
    }
    post.setPostModified(LocalDateTime.now());
    
    if (post.getPostStatus() == null) {
        post.setPostStatus("draft");
    }
    if (post.getPostType() == null) {
        post.setPostType("blog");
    }
    
    // Generate slug from title for post_name
    if (post.getPostName() == null || post.getPostName().isEmpty()) {
        String slug = generateSlug(post.getPostTitle());
        post.setPostName(slug);
    }
    
    // ✅✅✅ CRITICAL FIX: Set a GUID before saving (use temporary value)
    // The database requires guid to be NOT NULL
    if (post.getGuid() == null || post.getGuid().isEmpty()) {
        // Use a temporary GUID - will be updated after we have the ID
        post.setGuid("https://sqamer.com/?p=temp");
    }
    
    // Set default values for WordPress required fields
    if (post.getToPing() == null) post.setToPing("");
    if (post.getPinged() == null) post.setPinged("");
    if (post.getPostContentFiltered() == null) post.setPostContentFiltered("");
    if (post.getCommentStatus() == null) post.setCommentStatus("open");
    if (post.getPingStatus() == null) post.setPingStatus("open");
    if (post.getCommentCount() == null) post.setCommentCount(0L);
    if (post.getMenuOrder() == null) post.setMenuOrder(0);
    if (post.getPostMimeType() == null) post.setPostMimeType("");
    if (post.getPostPassword() == null) post.setPostPassword("");
    if (post.getPostParent() == null) post.setPostParent(0L);
    
    // Save image data separately
    String imageData = post.getPostImage();
    post.setPostImage(null);
    
    // ✅ Save the post (now guid is NOT null)
    Post saved = postRepository.save(post);
    System.out.println("✅ Post saved with ID: " + saved.getId());
    
    // ✅ Update GUID with the correct ID (WordPress standard)
    String correctGuid = "https://sqamer.com/?p=" + saved.getId();
    saved.setGuid(correctGuid);
    saved = postRepository.save(saved);
    System.out.println("✅ GUID updated to: " + correctGuid);
    
    // Save thumbnail to postmeta
    if (imageData != null && !imageData.isEmpty()) {
        postMetaService.saveThumbnail(saved.getId(), imageData);
        saved.setPostImage(imageData);
        System.out.println("✅ Image saved for post: " + saved.getId());
    }
    
    return saved;
}

// Helper method to generate slug
private String generateSlug(String title) {
    if (title == null) return "untitled";
    return title.toLowerCase()
            .replaceAll("[^a-z0-9\\s-]", "")
            .trim()
            .replaceAll("\\s+", "-")
            .replaceAll("-+", "-");
}
    @Transactional
    public Post updatePost(Long id, Post updatedPost) {
        Post existing = postRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Post not found with id: " + id));
        
        // Update basic fields
        existing.setPostTitle(updatedPost.getPostTitle());
        existing.setPostContent(updatedPost.getPostContent());
        existing.setPostExcerpt(updatedPost.getPostExcerpt());
        existing.setPostStatus(updatedPost.getPostStatus());
        existing.setPostModified(LocalDateTime.now());
        
        // Save post
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

    public Optional<Post> getPostById(Long id) {
        Optional<Post> post = postRepository.findById(id);
        post.ifPresent(p -> {
            String thumbnail = postMetaService.getThumbnail(p.getId());
            p.setPostImage(thumbnail);
        });
        return post;
    }

    @Transactional
    public void deletePost(Long id) {
        if (!postRepository.existsById(id)) {
            throw new RuntimeException("Post not found with id: " + id);
        }
        // Delete thumbnail from postmeta first
        postMetaService.deleteThumbnail(id);
        // Delete post
        postRepository.deleteById(id);
    }

    @Transactional
    public Post publishPost(Long id) {
        Post post = postRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Post not found with id: " + id));
        post.setPostStatus("publish");
        post.setPostModified(LocalDateTime.now());
        Post saved = postRepository.save(post);
        
        // Set image for response
        String thumbnail = postMetaService.getThumbnail(saved.getId());
        saved.setPostImage(thumbnail);
        
        return saved;
    }

    @Transactional
    public Post moveToDraft(Long id) {
        Post post = postRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Post not found with id: " + id));
        post.setPostStatus("draft");
        post.setPostModified(LocalDateTime.now());
        Post saved = postRepository.save(post);
        
        // Set image for response
        String thumbnail = postMetaService.getThumbnail(saved.getId());
        saved.setPostImage(thumbnail);
        
        return saved;
    }

    public Page<Post> getPublishedPosts(Pageable pageable) {
        Page<Post> posts = postRepository.findByPostStatus("publish", pageable);
        
        // Fetch thumbnails
        posts.forEach(post -> {
            String thumbnail = postMetaService.getThumbnail(post.getId());
            if (thumbnail != null && !thumbnail.isEmpty()) {
                post.setPostImage(thumbnail);
            }
        });
        
        return posts;
    }

    public long getTotalCount() {
        return postRepository.count();
    }

    public long getPublishedCount() {
        return postRepository.countByPostStatus("publish");
    }

    public long getDraftCount() {
        return postRepository.countByPostStatus("draft");
    }

    public Page<Post> searchPosts(String keyword, String status, Pageable pageable) {
        Page<Post> posts;
        if (status != null && !status.isEmpty()) {
            posts = postRepository.searchByKeywordAndStatus(keyword, status, pageable);
        } else {
            posts = postRepository.searchByKeyword(keyword, pageable);
        }
        
        // Fetch thumbnails
        posts.forEach(post -> {
            String thumbnail = postMetaService.getThumbnail(post.getId());
            if (thumbnail != null && !thumbnail.isEmpty()) {
                post.setPostImage(thumbnail);
            }
        });
        
        return posts;
    }
}