package com.taxpro.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "post_categories")
@IdClass(PostCategoryId.class)
public class PostCategory implements Serializable {
    
    @Id
    @Column(name = "post_id", columnDefinition = "BIGINT UNSIGNED")
    private Long postId;
    
    @Id
    @Column(name = "category_id", columnDefinition = "BIGINT UNSIGNED")
    private Long categoryId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", insertable = false, updatable = false)
    private Post post;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", insertable = false, updatable = false)
    private Category category;
    
    public PostCategory() {}
    
    public PostCategory(Long postId, Long categoryId) {
        this.postId = postId;
        this.categoryId = categoryId;
    }
    
    // Getters and Setters
    public Long getPostId() { return postId; }
    public void setPostId(Long postId) { this.postId = postId; }
    
    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }
    
    public Post getPost() { return post; }
    public void setPost(Post post) { this.post = post; }
    
    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PostCategory that = (PostCategory) o;
        return Objects.equals(postId, that.postId) && 
               Objects.equals(categoryId, that.categoryId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(postId, categoryId);
    }
}