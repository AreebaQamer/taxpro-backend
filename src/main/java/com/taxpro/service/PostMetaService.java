package com.taxpro.service;

import com.taxpro.entity.PostMeta;
import com.taxpro.repository.PostMetaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PostMetaService {

    @Autowired
    private PostMetaRepository postMetaRepository;
    
    @Transactional
    public void saveThumbnail(Long postId, String imageData) {
        System.out.println("=== saveThumbnail called ===");
        System.out.println("Post ID: " + postId);
        System.out.println("Image data length: " + (imageData != null ? imageData.length() : 0));
        
        if (imageData == null || imageData.isEmpty()) {
            System.out.println("No image data to save");
            return;
        }
        
        try {
            // Check if thumbnail already exists
            PostMeta existing = postMetaRepository.findByPostIdAndMetaKey(postId, "_thumbnail_url").orElse(null);
            
            if (existing != null) {
                existing.setMetaValue(imageData);
                postMetaRepository.save(existing);
                System.out.println("✅ Updated existing thumbnail");
            } else {
                PostMeta meta = new PostMeta();
                meta.setPostId(postId);
                meta.setMetaKey("_thumbnail_url");
                meta.setMetaValue(imageData);
                postMetaRepository.save(meta);
                System.out.println("✅ Created new thumbnail");
            }
        } catch (Exception e) {
            System.err.println("❌ Error in saveThumbnail: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public String getThumbnail(Long postId) {
        try {
            return postMetaRepository.findThumbnailByPostId(postId);
        } catch (Exception e) {
            return null;
        }
    }
    
    @Transactional
    public void deleteThumbnail(Long postId) {
        try {
            postMetaRepository.deleteByPostIdAndMetaKey(postId, "_thumbnail_url");
            System.out.println("✅ Deleted thumbnail for post: " + postId);
        } catch (Exception e) {
            System.err.println("❌ Error deleting thumbnail: " + e.getMessage());
        }
    }
}