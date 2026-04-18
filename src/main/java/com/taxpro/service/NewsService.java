package com.taxpro.service;

import com.taxpro.dto.NewsDTO;
import com.taxpro.entity.News;
import com.taxpro.repository.NewsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NewsService {
    
    @Autowired
    private NewsRepository newsRepository;
    
    // Convert Entity to DTO
    private NewsDTO convertToDTO(News news) {
        NewsDTO dto = new NewsDTO();
        dto.setId(news.getId());
        dto.setTitle(news.getTitle());
        dto.setContent(news.getContent());
        dto.setImage(news.getImage());
        dto.setAuthor(news.getAuthor());
        dto.setStatus(news.getStatus());
        dto.setViews(news.getViews());
        dto.setCreatedAt(news.getCreatedAt());
        dto.setUpdatedAt(news.getUpdatedAt());
        return dto;
    }
    
    // Convert DTO to Entity
    private News convertToEntity(NewsDTO dto) {
        News news = new News();
        news.setTitle(dto.getTitle());
        news.setContent(dto.getContent());
        news.setImage(dto.getImage());
        news.setAuthor(dto.getAuthor());
        news.setStatus(dto.getStatus());
        news.setViews(dto.getViews() != null ? dto.getViews() : 0);
        return news;
    }
    
    // Get trending news (top 5 by views) - for sidebar
    public List<NewsDTO> getTrendingNews() {
        List<News> newsList = newsRepository.findTop5ByStatusOrderByViewsDescCreatedAtDesc("published");
        return newsList.stream().map(this::convertToDTO).collect(Collectors.toList());
    }
    
    // Get all published news with pagination
    public Page<NewsDTO> getAllPublishedNews(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<News> newsPage = newsRepository.findByStatus("published", pageable);
        return newsPage.map(this::convertToDTO);
    }
    
    // Get single news by ID (increment views)
    @Transactional
    public NewsDTO getNewsById(Long id) {
        News news = newsRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("News not found with id: " + id));
        
        // Increment views
        news.setViews(news.getViews() + 1);
        newsRepository.save(news);
        
        return convertToDTO(news);
    }
    
    // Create news (Admin only)
    public NewsDTO createNews(NewsDTO newsDTO, String adminName) {
        News news = convertToEntity(newsDTO);
        news.setAuthor(adminName != null ? adminName : "Admin");
        news.setStatus(newsDTO.getStatus() != null ? newsDTO.getStatus() : "published");
        news.setViews(0);
        
        News savedNews = newsRepository.save(news);
        return convertToDTO(savedNews);
    }
    
    // Update news (Admin only)
    public NewsDTO updateNews(Long id, NewsDTO newsDTO) {
        News news = newsRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("News not found with id: " + id));
        
        if (newsDTO.getTitle() != null) news.setTitle(newsDTO.getTitle());
        if (newsDTO.getContent() != null) news.setContent(newsDTO.getContent());
        if (newsDTO.getImage() != null) news.setImage(newsDTO.getImage());
        if (newsDTO.getStatus() != null) news.setStatus(newsDTO.getStatus());
        
        News updatedNews = newsRepository.save(news);
        return convertToDTO(updatedNews);
    }
    
    // Delete news (Admin only)
    public void deleteNews(Long id) {
        newsRepository.deleteById(id);
    }
    
    // Get all news for admin (with pagination)
    public Page<NewsDTO> getAllNewsForAdmin(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<News> newsPage = newsRepository.findAllByOrderByCreatedAtDesc(pageable);
        return newsPage.map(this::convertToDTO);
    }
}