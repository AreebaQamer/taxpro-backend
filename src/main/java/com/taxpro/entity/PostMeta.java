package com.taxpro.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "wppw_postmeta")  // WordPress meta table
public class PostMeta {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "meta_id")
    private Long metaId;  // Meta entry ki unique ID
    
    @Column(name = "post_id")
    private Long postId;  // Kis post ki image hai
    
    @Column(name = "meta_key")
    private String metaKey;  // "_thumbnail_url" (WordPress standard)
    
    @Column(name = "meta_value", columnDefinition = "LONGTEXT")
    private String metaValue;  // Image ka base64 data
    
    // Getters and Setters
    public Long getMetaId() { return metaId; }
    public void setMetaId(Long metaId) { this.metaId = metaId; }
    
    public Long getPostId() { return postId; }
    public void setPostId(Long postId) { this.postId = postId; }
    
    public String getMetaKey() { return metaKey; }
    public void setMetaKey(String metaKey) { this.metaKey = metaKey; }
    
    public String getMetaValue() { return metaValue; }
    public void setMetaValue(String metaValue) { this.metaValue = metaValue; }
}