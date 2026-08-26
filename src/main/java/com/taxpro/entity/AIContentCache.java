package com.taxpro.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "ai_content_cache")
@Data
public class AIContentCache {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "service_key", unique = true, nullable = false, length = 100)
    private String serviceKey;

    @Column(name = "service_name", nullable = false)
    private String serviceName;

    @Column(name = "category", nullable = false)
    private String category;

    @Column(name = "content_json", columnDefinition = "JSON", nullable = false)
    private String contentJson;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}