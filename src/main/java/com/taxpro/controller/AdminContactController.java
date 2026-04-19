package com.taxpro.controller;

import com.taxpro.entity.Contact;
import com.taxpro.service.ContactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/contacts")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class AdminContactController {
    
    @Autowired
    private ContactService contactService;
    
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllContacts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Contact> contacts = contactService.getAllContacts(pageable);
        
        Map<String, Object> response = new HashMap<>();
        response.put("content", contacts.getContent());
        response.put("totalElements", contacts.getTotalElements());
        response.put("totalPages", contacts.getTotalPages());
        response.put("currentPage", contacts.getNumber());
        
        return ResponseEntity.ok(response);
    }
  
}