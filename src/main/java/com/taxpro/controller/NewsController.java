package com.taxpro.controller;

import com.taxpro.dto.NewsDTO;
import com.taxpro.service.NewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
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
    
    // Get trending news (top 5 by views) - FOR SIDEBAR
    @GetMapping("/news/trending")
    public ResponseEntity<List<NewsDTO>> getTrendingNews() {
        List<NewsDTO> trendingNews = newsService.getTrendingNews();
        return ResponseEntity.ok(trendingNews);
    }
    
    // Get all published news with pagination
    @GetMapping("/news")
    public ResponseEntity<Map<String, Object>> getAllPublishedNews(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Page<NewsDTO> newsPage = newsService.getAllPublishedNews(page, size);
        
        Map<String, Object> response = new HashMap<>();
        response.put("content", newsPage.getContent());
        response.put("totalElements", newsPage.getTotalElements());
        response.put("totalPages", newsPage.getTotalPages());
        response.put("currentPage", newsPage.getNumber());
        
        return ResponseEntity.ok(response);
    }
    
    // Get single news by ID (increments view count)
    @GetMapping("/news/{id}")
    public ResponseEntity<NewsDTO> getNewsById(@PathVariable Long id) {
        NewsDTO news = newsService.getNewsById(id);
        return ResponseEntity.ok(news);
    }
    
    // ================================================
    // ADMIN ENDPOINTS (require authentication)
    // ================================================
    
    // Create news
    @PostMapping("/admin/news")
    public ResponseEntity<NewsDTO> createNews(
            @RequestBody NewsDTO newsDTO,
            HttpServletRequest request) {
        
        // Get admin name from token (you can implement your auth logic)
        String adminName = (String) request.getAttribute("adminName");
        if (adminName == null) adminName = "Admin";
        
        NewsDTO createdNews = newsService.createNews(newsDTO, adminName);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdNews);
    }
    
    // Update news
    @PutMapping("/admin/news/{id}")
    public ResponseEntity<NewsDTO> updateNews(
            @PathVariable Long id,
            @RequestBody NewsDTO newsDTO) {
        NewsDTO updatedNews = newsService.updateNews(id, newsDTO);
        return ResponseEntity.ok(updatedNews);
    }
    
    // Delete news
    @DeleteMapping("/admin/news/{id}")
    public ResponseEntity<Map<String, String>> deleteNews(@PathVariable Long id) {
        newsService.deleteNews(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "News deleted successfully");
        return ResponseEntity.ok(response);
    }
    
    // Get all news for admin (including drafts)
    @GetMapping("/admin/news")
    public ResponseEntity<Map<String, Object>> getAllNewsForAdmin(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Page<NewsDTO> newsPage = newsService.getAllNewsForAdmin(page, size);
        
        Map<String, Object> response = new HashMap<>();
        response.put("content", newsPage.getContent());
        response.put("totalElements", newsPage.getTotalElements());
        response.put("totalPages", newsPage.getTotalPages());
        response.put("currentPage", newsPage.getNumber());
        
        return ResponseEntity.ok(response);
    }
}