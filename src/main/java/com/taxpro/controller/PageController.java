package com.taxpro.controller;

import com.taxpro.entity.PageContent;
import com.taxpro.service.PageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/pages")
@CrossOrigin(origins = "http://localhost:3000")
public class PageController {
    
    @Autowired
    private PageService pageService;
    
    @GetMapping("/structure")
    public ResponseEntity<Map<String, List<Map<String, String>>>> getStructure() {
        return ResponseEntity.ok(pageService.getPagesStructure());
    }
    
    @GetMapping("/{pageId}")
    public ResponseEntity<PageContent> getPage(@PathVariable String pageId) {
        return ResponseEntity.ok(pageService.getPage(pageId));
    }
    
    @PutMapping("/{pageId}")
    public ResponseEntity<PageContent> updatePage(
            @PathVariable String pageId,
            @RequestBody PageContent pageContent,
            @RequestHeader(value = "username", defaultValue = "admin") String username) {
        return ResponseEntity.ok(pageService.updatePage(pageId, pageContent, username));
    }
}