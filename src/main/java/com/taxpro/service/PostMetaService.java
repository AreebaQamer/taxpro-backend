package com.taxpro.service;

import com.taxpro.entity.PostMeta;
import com.taxpro.repository.PostMetaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class PostMetaService {

    @Autowired
    private PostMetaRepository postMetaRepository;

    public void saveThumbnail(Long postId, String imageUrl) {
        Optional<PostMeta> existing = postMetaRepository.findThumbnailMeta(postId);
        PostMeta meta = existing.orElse(new PostMeta());
        meta.setPostId(postId);
        meta.setMetaKey("_thumbnail_url"); // keep this
        meta.setMetaValue(imageUrl);
        postMetaRepository.save(meta);
    }

    public String getThumbnail(Long postId) {
        return postMetaRepository.findThumbnailMeta(postId)
                .map(PostMeta::getMetaValue)
                .orElse(null);
    }
}