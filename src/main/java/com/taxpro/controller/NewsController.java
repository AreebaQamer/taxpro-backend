package com.taxpro.controller;

import com.taxpro.entity.News;
import com.taxpro.service.NewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class NewsController {
    
    @Autowired
    private NewsService newsService;
    
    // ================================================
    // PUBLIC ENDPOINTS
    // ================================================
    
    @GetMapping("/news/trending")
    public ResponseEntity<List<News>> getTrendingNews() {
        List<News> trendingNews = newsService.getTrendingNews();
        return ResponseEntity.ok(trendingNews);
    }
    
    @GetMapping("/news")
    public ResponseEntity<Map<String, Object>> getAllPublishedNews(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Page<News> newsPage = newsService.getAllPublishedNews(page, size);
        
        Map<String, Object> response = new HashMap<>();
        response.put("content", newsPage.getContent());
        response.put("totalElements", newsPage.getTotalElements());
        response.put("totalPages", newsPage.getTotalPages());
        response.put("currentPage", newsPage.getNumber());
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/news/{id}")
    public ResponseEntity<News> getNewsById(@PathVariable Long id) {
        News news = newsService.getNewsById(id);
        return ResponseEntity.ok(news);
    }
    
    // ================================================
    // ADMIN ENDPOINTS
    // ================================================
    
    @PostMapping("/admin/news")
    public ResponseEntity<News> createNews(
            @RequestBody News news,
            HttpServletRequest request) {
        
        String adminName = (String) request.getAttribute("adminName");
        if (adminName == null) adminName = "Admin";
        
        News createdNews = newsService.createNews(news, adminName);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdNews);
    }
    
    @PutMapping("/admin/news/{id}")
    public ResponseEntity<News> updateNews(
            @PathVariable Long id,
            @RequestBody News news) {
        News updatedNews = newsService.updateNews(id, news);
        return ResponseEntity.ok(updatedNews);
    }
    
    @DeleteMapping("/admin/news/{id}")
    public ResponseEntity<Map<String, String>> deleteNews(@PathVariable Long id) {
        newsService.deleteNews(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "News deleted successfully");
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/admin/news")
    public ResponseEntity<Map<String, Object>> getAllNewsForAdmin(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Page<News> newsPage = newsService.getAllNewsForAdmin(page, size);
        
        Map<String, Object> response = new HashMap<>();
        response.put("content", newsPage.getContent());
        response.put("totalElements", newsPage.getTotalElements());
        response.put("totalPages", newsPage.getTotalPages());
        response.put("currentPage", newsPage.getNumber());
        
        return ResponseEntity.ok(response);
    }
}