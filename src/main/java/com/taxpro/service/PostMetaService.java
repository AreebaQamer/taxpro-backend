package com.taxpro.service;

import com.taxpro.entity.PostMeta;
import com.taxpro.repository.PostMetaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;  // ✅ YEH IMPORT ADD KARO


@Service
public class PostMetaService {

    @Autowired
    private PostMetaRepository postMetaRepository;
    
    @Transactional
    public void saveThumbnail(Long postId, String imageData) {
        if (imageData == null || imageData.isEmpty()) {
            System.out.println("No image to save for post: " + postId);
            return;
        }
        
        try {
            // Check if thumbnail already exists
            Optional<PostMeta> existing = postMetaRepository.findThumbnailMetaByPostId(postId);
            
            if (existing.isPresent()) {
                // Update existing
                PostMeta meta = existing.get();
                meta.setMetaValue(imageData);
                postMetaRepository.save(meta);
                System.out.println("Updated thumbnail for post: " + postId);
            } else {
                // Create new
                PostMeta meta = new PostMeta();
                meta.setPostId(postId);
                meta.setMetaKey("_thumbnail_url");
                meta.setMetaValue(imageData);
                postMetaRepository.save(meta);
                System.out.println("Saved new thumbnail for post: " + postId);
            }
        } catch (Exception e) {
            System.err.println("Error saving thumbnail: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public String getThumbnail(Long postId) {
        try {
            return postMetaRepository.findThumbnailByPostId(postId);
        } catch (Exception e) {
            System.err.println("Error getting thumbnail: " + e.getMessage());
            return null;
        }
    }
    
    @Transactional
    public void deleteThumbnail(Long postId) {
        try {
            postMetaRepository.deleteByPostIdAndMetaKey(postId, "_thumbnail_url");
            System.out.println("Deleted thumbnail for post: " + postId);
        } catch (Exception e) {
            System.err.println("Error deleting thumbnail: " + e.getMessage());
        }
    }
}