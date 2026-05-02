package com.taxpro.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "post_categories")
public class PostCategory {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "post_id")
    private Long postId;
    
    @Column(name = "category_id")
    private Long categoryId;
    
    @ManyToOne
    @JoinColumn(name = "post_id", insertable = false, updatable = false)
    private Post post;
    
    @ManyToOne
    @JoinColumn(name = "category_id", insertable = false, updatable = false)
    private Category category;
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getPostId() { return postId; }
    public void setPostId(Long postId) { this.postId = postId; }
    
    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }
    
    public Post getPost() { return post; }
    public void setPost(Post post) { this.post = post; }
    
    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }
}