package com.taxpro.controller;

import com.taxpro.dto.ContentRequestDTO;
import com.taxpro.dto.ContentResponseDTO;
import com.taxpro.service.AIContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
@CrossOrigin(origins = "*")
public class AIContentController {

    @Autowired
    private AIContentService aiContentService;

    @PostMapping("/generate-article")
    public ResponseEntity<?> generateArticle(@RequestBody ContentRequestDTO request) {
        try {
            System.out.println("📝 Generating article for: " + request.getServiceName());
            ContentResponseDTO content = aiContentService.generateArticle(request);
            return ResponseEntity.ok(content);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }
}