package com.taxpro.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "wppw_postmeta")
public class PostMeta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "meta_id")
    private Long metaId;

    @Column(name = "post_id")
    private Long postId;

    @Column(name = "meta_key")
    private String metaKey;

    @Column(name = "meta_value")
    private String metaValue;

    public Long getMetaId() { return metaId; }
    public void setMetaId(Long metaId) { this.metaId = metaId; }
    public Long getPostId() { return postId; }
    public void setPostId(Long postId) { this.postId = postId; }
    public String getMetaKey() { return metaKey; }
    public void setMetaKey(String metaKey) { this.metaKey = metaKey; }
    public String getMetaValue() { return metaValue; }
    public void setMetaValue(String metaValue) { this.metaValue = metaValue; }
}