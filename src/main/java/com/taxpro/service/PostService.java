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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;
    
    @Autowired
    private PostMetaService postMetaService;

@Transactional
public Page<Post> getAllPosts(String status, Pageable pageable) {
    System.out.println("=== getAllPosts CALLED ===");
    System.out.println("Status filter: " + status);
    
    Page<Post> posts;
    if (status != null && !status.isEmpty()) {
        posts = postRepository.findByPostStatus(status, pageable);
        System.out.println("Filtering by status: " + status);
    } else {
        // ✅ Get ALL posts explicitly with sorting
        posts = postRepository.findAll(PageRequest.of(
            pageable.getPageNumber(),
            pageable.getPageSize(),
            Sort.by(Sort.Direction.DESC, "postDate")
        ));
        System.out.println("Getting ALL posts (no filter)");
    }
    
    System.out.println("Total posts in page: " + posts.getNumberOfElements());
    System.out.println("Total elements in DB: " + posts.getTotalElements());
    
    // Debug: Print each post's status
    posts.forEach(post -> {
        System.out.println("  Post ID: " + post.getId() + 
                           " | Title: " + post.getPostTitle() + 
                           " | Status: " + post.getPostStatus() +
                           " | Type: " + post.getPostType());
    });
    
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
        
        LocalDateTime now = LocalDateTime.now();
        
        // ✅ Set required fields for NOT NULL columns
        if (post.getPostDate() == null) post.setPostDate(now);
        if (post.getPostModified() == null) post.setPostModified(now);
        
        if (post.getPostStatus() == null) post.setPostStatus("draft");
        if (post.getPostType() == null) post.setPostType("blog");
        
        // ✅ Generate post_name (slug) - CRITICAL
        if (post.getPostName() == null || post.getPostName().isEmpty()) {
            String slug = generateSlug(post.getPostTitle());
            post.setPostName(slug);
            System.out.println("Generated post_name: " + slug);
        }
        
        // ✅ Set GUID - CRITICAL
        if (post.getGuid() == null || post.getGuid().isEmpty()) {
            post.setGuid("https://sqamer.com/?p=temp");
        }
        
        // ✅ Set all other NOT NULL columns with defaults
        if (post.getPostAuthor() == null) post.setPostAuthor(1L);
        if (post.getPostExcerpt() == null) post.setPostExcerpt("");
        if (post.getPostContent() == null) post.setPostContent("");
        if (post.getPostTitle() == null) post.setPostTitle("");
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
        
        // Save image separately
        String imageData = post.getPostImage();
        post.setPostImage(null);
        
        // ✅ Save the post
        Post saved = postRepository.save(post);
        System.out.println("✅ Post saved with ID: " + saved.getId());
        
        // ✅ Update GUID with correct ID (WordPress standard)
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
        
        existing.setPostTitle(updatedPost.getPostTitle());
        existing.setPostContent(updatedPost.getPostContent());
        existing.setPostExcerpt(updatedPost.getPostExcerpt());
        existing.setPostStatus(updatedPost.getPostStatus());
        existing.setPostModified(LocalDateTime.now());
        
        // Update slug if title changed
        if (updatedPost.getPostTitle() != null && !updatedPost.getPostTitle().equals(existing.getPostTitle())) {
            String newSlug = generateSlug(updatedPost.getPostTitle());
            existing.setPostName(newSlug);
        }
        
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
        postMetaService.deleteThumbnail(id);
        postRepository.deleteById(id);
    }

    @Transactional
    public Post publishPost(Long id) {
        Post post = postRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Post not found with id: " + id));
        post.setPostStatus("publish");
        post.setPostModified(LocalDateTime.now());
        Post saved = postRepository.save(post);
        
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
        
        String thumbnail = postMetaService.getThumbnail(saved.getId());
        saved.setPostImage(thumbnail);
        
        return saved;
    }

    public Page<Post> getPublishedPosts(Pageable pageable) {
        Page<Post> posts = postRepository.findByPostStatus("publish", pageable);
        
        posts.forEach(post -> {
            String thumbnail = postMetaService.getThumbnail(post.getId());
            if (thumbnail != null && !thumbnail.isEmpty()) {
                post.setPostImage(thumbnail);
            }
        });
        
        return posts;
    }

    public Page<Post> getPublishedPostsByType(Pageable pageable, String postType) {
        Page<Post> posts = postRepository.findByPostStatusAndPostType("publish", postType, pageable);
        
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
        
        posts.forEach(post -> {
            String thumbnail = postMetaService.getThumbnail(post.getId());
            if (thumbnail != null && !thumbnail.isEmpty()) {
                post.setPostImage(thumbnail);
            }
        });
        
        return posts;
    }
}