package com.taxpro.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties; // Import this
import java.time.LocalDateTime;

@Entity
@Table(name = "wppw_posts")
@Schema(description = "Blog Post Entity")
public class Post {

    @Transient
    private String postImage;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    @Schema(description = "Unique post ID", example = "1")
    private Long id;
    
    // ... (baaki fields wese hi rahenge: postTitle, postContent, etc.) ...

    @Column(name = "post_title")
    private String postTitle;
    
    @Column(name = "post_content", columnDefinition = "LONGTEXT")
    private String postContent;
    
    @Column(name = "post_excerpt")
    private String postExcerpt;
    
    @Column(name = "post_date")
    private LocalDateTime postDate;
    
    @Column(name = "post_modified")
    private LocalDateTime postModified;
    
    @Column(name = "guid", nullable = false, updatable = false)
    private String guid;
    
    @Column(name = "post_status")
    private String postStatus;
    
    @Column(name = "postType")
    private String postType = "blog";
        
    @Column(name = "post_author")
    private Long postAuthor;

    // =========================================================
    // 👇 YEH NAYA FIELD ADD KAREIN (Category Relationship) 👇
    // =========================================================
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id") // Database mein foreign key ban jayegi
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"}) // Lazy loading error se bachne ke liye
    private Category category;

    // Aapki baaki fields (toPing, pinged, etc.) ...
    @Column(name = "to_ping", columnDefinition = "TEXT") private String toPing = "";
    @Column(name = "pinged", columnDefinition = "TEXT") private String pinged = "";
    @Column(name = "post_content_filtered", columnDefinition = "LONGTEXT") private String postContentFiltered = "";
    @Column(name = "comment_status") private String commentStatus = "open";
    @Column(name = "ping_status") private String pingStatus = "open";
    @Column(name = "comment_count") private Long commentCount = 0L;
    @Column(name = "menu_order") private Integer menuOrder = 0;
    @Column(name = "post_mime_type") private String postMimeType = "";
    @Column(name = "post_name") private String postName = "";
    @Column(name = "post_password") private String postPassword = "";
    @Column(name = "post_parent") private Long postParent = 0L;
    
    public Post() {}
    
    // =========================================================
    // 👇 CATEGORY KE GETTER AUR SETTER 👇
    // =========================================================
    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    // React frontend ko `post.categoryName` chahiye hota hai, 
    // isliye yeh helper method JSON mein "categoryName" add kar dega
    public String getCategoryName() {
        if (this.category != null) {
            return this.category.getName();
        }
        return null;
    }

    // ... (Aapke baaki saare getters aur setters (getId, getPostTitle, etc.) yahan aayenge) ...

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getPostTitle() { return postTitle; }
    public void setPostTitle(String postTitle) { this.postTitle = postTitle; }
    public String getPostContent() { return postContent; }
    public void setPostContent(String postContent) { this.postContent = postContent; }
    public String getPostExcerpt() { return postExcerpt; }
    public void setPostExcerpt(String postExcerpt) { this.postExcerpt = postExcerpt; }
    public LocalDateTime getPostDate() { return postDate; }
    public void setPostDate(LocalDateTime postDate) { this.postDate = postDate; }
    public LocalDateTime getPostModified() { return postModified; }
    public void setPostModified(LocalDateTime postModified) { this.postModified = postModified; }
    public String getGuid() { return guid; }
    public void setGuid(String guid) { this.guid = guid; }
    public String getPostStatus() { return postStatus; }
    public void setPostStatus(String postStatus) { this.postStatus = postStatus; }
    public String getPostType() { return postType; }
    public void setPostType(String postType) { this.postType = postType; }
    public Long getPostAuthor() { return postAuthor; }
    public void setPostAuthor(Long postAuthor) { this.postAuthor = postAuthor; }
    public String getToPing() { return toPing; }
    public void setToPing(String toPing) { this.toPing = toPing; }
    public String getPinged() { return pinged; }
    public void setPinged(String pinged) { this.pinged = pinged; }
    public String getPostContentFiltered() { return postContentFiltered; }
    public void setPostContentFiltered(String postContentFiltered) { this.postContentFiltered = postContentFiltered; }
    public String getCommentStatus() { return commentStatus; }
    public void setCommentStatus(String commentStatus) { this.commentStatus = commentStatus; }
    public String getPingStatus() { return pingStatus; }
    public void setPingStatus(String pingStatus) { this.pingStatus = pingStatus; }
    public Long getCommentCount() { return commentCount; }
    public void setCommentCount(Long commentCount) { this.commentCount = commentCount; }
    public Integer getMenuOrder() { return menuOrder; }
    public void setMenuOrder(Integer menuOrder) { this.menuOrder = menuOrder; }
    public String getPostMimeType() { return postMimeType; }
    public void setPostMimeType(String postMimeType) { this.postMimeType = postMimeType; }
    public String getPostName() { return postName; }
    public void setPostName(String postName) { this.postName = postName; }
    public String getPostPassword() { return postPassword; }
    public void setPostPassword(String postPassword) { this.postPassword = postPassword; }
    public Long getPostParent() { return postParent; }
    public void setPostParent(Long postParent) { this.postParent = postParent; }
    public String getPostImage() { return postImage; }
    public void setPostImage(String postImage) { this.postImage = postImage; }

    @PrePersist
    private void generateGuid() {
        if (this.guid == null || this.guid.isEmpty()) {
            this.guid = "https://sqamer.com/?p=temp";
        }
    }
    public String getCategorySlug() {
    if (this.category != null) {
        return this.category.getSlug();
    }
    return null;
}
}