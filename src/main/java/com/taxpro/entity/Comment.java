package com.taxpro.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "wppw_comments")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_ID")
    private Long id;

    @Column(name = "comment_post_ID")
    private Long postId;

    @Column(name = "comment_author")
    private String commentAuthor;

    @Column(name = "comment_author_email")
    private String commentAuthorEmail;

    @Column(name = "comment_author_url")
    private String commentAuthorUrl;

    @Column(name = "comment_author_IP")
    private String commentAuthorIP;

    @Column(name = "comment_date")
    private LocalDateTime commentDate;

    @Column(name = "comment_date_gmt")
    private LocalDateTime commentDateGmt;

    @Column(name = "comment_content", columnDefinition = "TEXT")
    private String commentContent;

    @Column(name = "comment_karma")
    private Integer commentKarma = 0;

    // 0 = pending, 1 = approved, spam = spam, trash = trash
    @Column(name = "comment_approved")
    private String commentApproved = "0";

    @Column(name = "comment_agent")
    private String commentAgent;

    @Column(name = "comment_type")
    private String commentType = "comment";

    @Column(name = "comment_parent")
    private Long commentParent = 0L;

    @Column(name = "user_id")
    private Long userId = 0L;

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        if (this.commentDate    == null) this.commentDate    = now;
        if (this.commentDateGmt == null) this.commentDateGmt = now;
    }

    // ── Getters & Setters ─────────────────────────────────────────────────────
    public Long getId()                            { return id; }
    public void setId(Long id)                     { this.id = id; }

    public Long getPostId()                        { return postId; }
    public void setPostId(Long postId)             { this.postId = postId; }

    public String getCommentAuthor()               { return commentAuthor; }
    public void setCommentAuthor(String v)         { this.commentAuthor = v; }

    public String getCommentAuthorEmail()          { return commentAuthorEmail; }
    public void setCommentAuthorEmail(String v)    { this.commentAuthorEmail = v; }

    public String getCommentAuthorUrl()            { return commentAuthorUrl; }
    public void setCommentAuthorUrl(String v)      { this.commentAuthorUrl = v; }

    public String getCommentAuthorIP()             { return commentAuthorIP; }
    public void setCommentAuthorIP(String v)       { this.commentAuthorIP = v; }

    public LocalDateTime getCommentDate()          { return commentDate; }
    public void setCommentDate(LocalDateTime v)    { this.commentDate = v; }

    public LocalDateTime getCommentDateGmt()       { return commentDateGmt; }
    public void setCommentDateGmt(LocalDateTime v) { this.commentDateGmt = v; }

    public String getCommentContent()              { return commentContent; }
    public void setCommentContent(String v)        { this.commentContent = v; }

    public Integer getCommentKarma()               { return commentKarma; }
    public void setCommentKarma(Integer v)         { this.commentKarma = v; }

    public String getCommentApproved()             { return commentApproved; }
    public void setCommentApproved(String v)       { this.commentApproved = v; }

    public String getCommentAgent()                { return commentAgent; }
    public void setCommentAgent(String v)          { this.commentAgent = v; }

    public String getCommentType()                 { return commentType; }
    public void setCommentType(String v)           { this.commentType = v; }

    public Long getCommentParent()                 { return commentParent; }
    public void setCommentParent(Long v)           { this.commentParent = v; }

    public Long getUserId()                        { return userId; }
    public void setUserId(Long v)                  { this.userId = v; }
}