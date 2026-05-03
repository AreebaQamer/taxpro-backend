package com.taxpro.entity;

import java.io.Serializable;
import java.util.Objects;

public class PostCategoryId implements Serializable {
    
    private Long postId;
    private Long categoryId;
    
    public PostCategoryId() {}
    
    public PostCategoryId(Long postId, Long categoryId) {
        this.postId = postId;
        this.categoryId = categoryId;
    }
    
    public Long getPostId() { return postId; }
    public void setPostId(Long postId) { this.postId = postId; }
    
    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PostCategoryId that = (PostCategoryId) o;
        return Objects.equals(postId, that.postId) && 
               Objects.equals(categoryId, that.categoryId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(postId, categoryId);
    }
}