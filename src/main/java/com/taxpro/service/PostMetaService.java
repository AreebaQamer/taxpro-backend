package com.taxpro.service;

import com.taxpro.entity.PostMeta;
import com.taxpro.repository.PostMetaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// ✅ YEH IMPORT ADD KARO
import java.util.Optional;

@Service
public class PostMetaService {
    
    @Autowired
    private PostMetaRepository postMetaRepository;
    
    @Transactional
    public void saveThumbnail(Long postId, String imageData) {
        Optional<PostMeta> existing = postMetaRepository.findByPostIdAndMetaKey(postId, "_thumbnail_url");
        
        if (existing.isPresent()) {
            PostMeta meta = existing.get();
            meta.setMetaValue(imageData);
            postMetaRepository.save(meta);
        } else {
            PostMeta meta = new PostMeta();
            meta.setPostId(postId);
            meta.setMetaKey("_thumbnail_url");
            meta.setMetaValue(imageData);
            postMetaRepository.save(meta);
        }
    }
    
    public String getThumbnail(Long postId) {
        Optional<PostMeta> meta = postMetaRepository.findByPostIdAndMetaKey(postId, "_thumbnail_url");
        return meta.map(PostMeta::getMetaValue).orElse(null);
    }
    
    @Transactional
    public void deleteThumbnail(Long postId) {
        postMetaRepository.deleteByPostIdAndMetaKey(postId, "_thumbnail_url");
    }
}