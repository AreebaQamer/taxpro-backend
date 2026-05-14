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
        Page<Post> posts;
        
        // ✅ Sab posts dikhao (blog aur news dono)
        if (status != null && !status.isEmpty()) {
            posts = postRepository.findByPostStatus(status, pageable);
        } else {
            posts = postRepository.findAll(pageable);
        }

        // Fetch thumbnails for each post
        posts.forEach(post -> {
            String thumbnail = postMetaService.getThumbnail(post.getId());
            if (thumbnail != null && !thumbnail.isEmpty()) {
                post.setPostImage(thumbnail);
            }
        });

        return posts;
    }
    
 // ✅ Create post method
    @Transactional
    public Post createPost(Post post) {
        System.out.println("=== CREATE POST WITH IMAGE ===");
        System.out.println("Title: " + post.getPostTitle());
        System.out.println("PostType: " + post.getPostType()); // 'blog' ya 'news'
        
        // Set default values
        if (post.getPostDate() == null) {
            post.setPostDate(LocalDateTime.now());
            post.setPostDateGmt(LocalDateTime.now());
        }
        post.setPostModified(LocalDateTime.now());
        post.setPostModifiedGmt(LocalDateTime.now());
        
        if (post.getPostStatus() == null) {
            post.setPostStatus("draft");
        }
        
        // ✅ Default postType set karo (blog ya news)
        if (post.getPostType() == null || post.getPostType().isEmpty()) {
            post.setPostType("blog");
        }
        
        // Generate slug from title
        if (post.getPostName() == null || post.getPostName().isEmpty()) {
            post.setPostName(generateSlug(post.getPostTitle()));
        }
        
        // Save image data separately
        String imageData = post.getPostImage();
        post.setPostImage(null);
        
        // Save the post
        Post saved = postRepository.save(post);
        System.out.println("✅ Post saved with ID: " + saved.getId());
        System.out.println("✅ PostType: " + saved.getPostType());
        
        // Save thumbnail to postmeta
        if (imageData != null && !imageData.isEmpty()) {
            postMetaService.saveThumbnail(saved.getId(), imageData);
            saved.setPostImage(imageData);
            System.out.println("✅ Image saved for post: " + saved.getId());
        }
        
        return saved;
    }
    
    @Transactional
    public Post updatePost(Long id, Post updatedPost) {
        Post existing = postRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Post not found"));
        
        existing.setPostTitle(updatedPost.getPostTitle());
        existing.setPostContent(updatedPost.getPostContent());
        existing.setPostExcerpt(updatedPost.getPostExcerpt());
        existing.setPostStatus(updatedPost.getPostStatus());
        existing.setPostType(updatedPost.getPostType()); // ✅ Update postType
        existing.setPostModified(LocalDateTime.now());
        
        Post saved = postRepository.save(existing);
        
        if (updatedPost.getPostImage() != null && !updatedPost.getPostImage().isEmpty()) {
            postMetaService.saveThumbnail(saved.getId(), updatedPost.getPostImage());
            saved.setPostImage(updatedPost.getPostImage());
        } else {
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
  private String generateSlug(String title) {
        if (title == null || title.isEmpty()) {
            return "untitled";
        }
        return title.toLowerCase()
            .replaceAll("[^a-z0-9\\s-]", "")  // Remove special chars
            .replaceAll("\\s+", "-")           // Spaces to hyphens
            .replaceAll("-+", "-")             // Remove multiple hyphens
            .trim();
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