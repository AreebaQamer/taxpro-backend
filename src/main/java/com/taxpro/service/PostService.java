package com.taxpro.service;

import com.taxpro.entity.Post;
import com.taxpro.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Optional;

// ✅ ADD THIS MISSING IMPORT
import com.taxpro.service.PostMetaService;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private PostMetaService postMetaService;  // Now import is added

    // Admin — sare posts (status filter ke saath)
    public Page<Post> getAllPosts(String status, Pageable pageable) {
        Page<Post> posts = status != null && !status.isEmpty()
            ? postRepository.findByPostStatusAndPostType(status, "post", pageable)
            : postRepository.findByPostType("post", pageable);

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

    public Post createPost(Post post) {
        if (post.getPostDate() == null) {
            post.setPostDate(LocalDateTime.now());
        }
        post.setPostModified(LocalDateTime.now());
        if (post.getPostStatus() == null) {
            post.setPostStatus("draft");
        }
        
        // ✅ SIRF POST SAVE KARO - IMAGE MAT SAVE KARO
        return postRepository.save(post);
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
        LocalDateTime cutoff = LocalDateTime.of(2026, 1, 1, 0, 0);
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