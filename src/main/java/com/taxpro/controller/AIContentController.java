package com.taxpro.controller;

import com.taxpro.entity.ContentRequestDTO;
import com.taxpro.entity.ContentResponseDTO;
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

    @PostMapping("/generate-service-content")
    public ResponseEntity<?> generateServiceContent(@RequestBody ContentRequestDTO request) {
        try {
            System.out.println("📝 Generating content for: " + request.getServiceName());
            // ✅ Method name match karein
            ContentResponseDTO content = aiContentService.generateServiceContent(request);
            return ResponseEntity.ok(content);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }
}