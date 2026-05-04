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
        if (imageData == null || imageData.isEmpty()) {
            return;
        }
        
        // Check if thumbnail already exists
        PostMeta existing = postMetaRepository.findThumbnailMetaByPostId(postId).orElse(null);
        
        if (existing != null) {
            // Update existing
            existing.setMetaValue(imageData);
            postMetaRepository.save(existing);
        } else {
            // Create new
            PostMeta meta = new PostMeta();
            meta.setPostId(postId);
            meta.setMetaKey("_thumbnail_url");
            meta.setMetaValue(imageData);
            postMetaRepository.save(meta);
        }
    }
    
    public String getThumbnail(Long postId) {
        return postMetaRepository.findThumbnailByPostId(postId);
    }
    
    @Transactional
    public void deleteThumbnail(Long postId) {
        postMetaRepository.deleteByPostIdAndMetaKey(postId, "_thumbnail_url");
    }
}