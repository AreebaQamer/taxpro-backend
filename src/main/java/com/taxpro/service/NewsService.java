package com.taxpro.service;

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

@Service
public class NewsService {
    
    @Autowired
    private NewsRepository newsRepository;
    
    public List<News> getTrendingNews() {
        return newsRepository.findTop5ByStatusOrderByViewsDescCreatedAtDesc("published");
    }
    
    public Page<News> getAllPublishedNews(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return newsRepository.findByStatus("published", pageable);
    }
    
    @Transactional
    public News getNewsById(Long id) {
        News news = newsRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("News not found with id: " + id));
        news.setViews(news.getViews() + 1);
        return newsRepository.save(news);
    }
    
    public News createNews(News news, String adminName) {
        news.setAuthor(adminName != null ? adminName : "Admin");
        news.setStatus(news.getStatus() != null ? news.getStatus() : "published");
        news.setViews(0);
        return newsRepository.save(news);
    }
    
    public News updateNews(Long id, News newsDetails) {
        News news = newsRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("News not found with id: " + id));
        
        if (newsDetails.getTitle() != null) news.setTitle(newsDetails.getTitle());
        if (newsDetails.getContent() != null) news.setContent(newsDetails.getContent());
        if (newsDetails.getImage() != null) news.setImage(newsDetails.getImage());
        if (newsDetails.getStatus() != null) news.setStatus(newsDetails.getStatus());
        
        return newsRepository.save(news);
    }
    
    public void deleteNews(Long id) {
        newsRepository.deleteById(id);
    }
    
    public Page<News> getAllNewsForAdmin(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return newsRepository.findAll(pageable);
    }
}