package com.taxpro.service;

import com.taxpro.entity.Post;
import com.taxpro.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Optional;


@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    // Admin — sare posts (status filter ke saath)
  public Page<Post> getAllPosts(String status, Pageable pageable) {
    Page<Post> posts = status != null && !status.isEmpty()
        ? postRepository.findByPostStatusAndPostType(status, "post", pageable)  // ← CHANGE
        : postRepository.findByPostType("post", pageable);  // ← CHANGE

    posts.forEach(post -> {
        String imageUrl = postRepository.findThumbnailByPostId(post.getId());
        if (imageUrl != null) {
            post.setGuid(imageUrl);
        }
    });

    return posts;
}

    // Admin — single post
    public Optional<Post> getPostById(Long id) {
        return postRepository.findById(id);
    }

   @Autowired
private PostMetaService postMetaService;

public Post createPost(Post post) {
    try {
        System.out.println("========== CREATE POST SERVICE START ==========");
        System.out.println("Received Post Title: " + post.getPostTitle());
        System.out.println("Post Content Length: " + (post.getPostContent() != null ? post.getPostContent().length() : 0));
        System.out.println("Post Status: " + post.getPostStatus());
        System.out.println("Post Author: " + post.getPostAuthor());
        System.out.println("Post Type: " + post.getPostType());
        
        // Set default values
        if (post.getPostDate() == null) {
            post.setPostDate(LocalDateTime.now());
            System.out.println("Set postDate: " + post.getPostDate());
        }
        
        post.setPostModified(LocalDateTime.now());
        System.out.println("Set postModified: " + post.getPostModified());
        
        if (post.getPostStatus() == null) {
            post.setPostStatus("draft");
            System.out.println("Set default status: draft");
        }
        
        if (post.getPostType() == null) {
            post.setPostType("post");
            System.out.println("Set default postType: post");
        }
        
        if (post.getPostAuthor() == null) {
            post.setPostAuthor(1L);
            System.out.println("Set default author: 1");
        }
        
        // Set all other fields to prevent null values
        if (post.getToPing() == null) post.setToPing("");
        if (post.getPinged() == null) post.setPinged("");
        if (post.getPostContentFiltered() == null) post.setPostContentFiltered("");
        if (post.getCommentStatus() == null) post.setCommentStatus("open");
        if (post.getPingStatus() == null) post.setPingStatus("open");
        if (post.getCommentCount() == null) post.setCommentCount(0L);
        if (post.getMenuOrder() == null) post.setMenuOrder(0);
        if (post.getPostMimeType() == null) post.setPostMimeType("");
        if (post.getPostName() == null) post.setPostName("");
        if (post.getPostPassword() == null) post.setPostPassword("");
        if (post.getPostParent() == null) post.setPostParent(0L);
        if (post.getGuid() == null) post.setGuid("");
        
        System.out.println("Attempting to save post to database...");
        Post saved = postRepository.save(post);
        System.out.println("✅ Post saved successfully with ID: " + saved.getId());
        System.out.println("========== CREATE POST SERVICE END ==========");
        
        return saved;
        
    } catch (Exception e) {
        System.err.println("❌ ERROR in createPost: " + e.getMessage());
        e.printStackTrace();
        throw new RuntimeException("Failed to create post: " + e.getMessage(), e);
    }
}
   public Post updatePost(Long id, Post updatedPost) {
    Post existing = postRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Post not found"));
    existing.setPostTitle(updatedPost.getPostTitle());
    existing.setPostContent(updatedPost.getPostContent());
    existing.setPostExcerpt(updatedPost.getPostExcerpt());
    existing.setPostStatus(updatedPost.getPostStatus());
    existing.setPostModified(LocalDateTime.now());
    
    Post saved = postRepository.save(existing);
    
    // Image update karo
    if (updatedPost.getPostImage() != null && !updatedPost.getPostImage().isEmpty()) {
        postMetaService.saveThumbnail(saved.getId(), updatedPost.getPostImage());
    }
    
    return saved;
}

    // Admin — delete
    public void deletePost(Long id) {
        if (!postRepository.existsById(id)) {
            throw new RuntimeException("Post not found");
        }
        postRepository.deleteById(id);
    }

    // Admin — publish
    public Post publishPost(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        post.setPostStatus("publish");
        post.setPostModified(LocalDateTime.now());
        return postRepository.save(post);
    }

    // Admin — draft
    public Post moveToDraft(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        post.setPostStatus("draft");
        post.setPostModified(LocalDateTime.now());
        return postRepository.save(post);
    }

    // Stats
    public long getTotalCount() {
        return postRepository.count();
    }

    public long getPublishedCount() {
        return postRepository.countByPostStatus("publish");
    }

    public long getDraftCount() {
        return postRepository.countByPostStatus("draft");
    }

   public Page<Post> getPublishedPosts(Pageable pageable) {
    return postRepository.findByPostStatus("publish", pageable);
}

  // Yeh method update karo
public Page<Post> getPublishedPostsBefore2026(Pageable pageable) {
    LocalDateTime cutoff = LocalDateTime.of(2026, 1, 1, 0, 0); // ✅ LocalDateTime pass karo
    return postRepository.findPublishedPostsBefore2026(cutoff, pageable);
}
    public Optional<Post> getPublishedPostById(Long id) {
        return postRepository.findPublishedPostById(id);
    }
    // Add to PostService.java
public Page<Post> searchPosts(String keyword, String status, Pageable pageable) {
    if (status != null && !status.isEmpty()) {
        return postRepository.searchByKeywordAndStatus(keyword, status, pageable);
    } else {
        return postRepository.searchByKeyword(keyword, pageable);
    }
}
}