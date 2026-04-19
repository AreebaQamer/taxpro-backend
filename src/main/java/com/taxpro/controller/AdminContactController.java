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
    
    @GetMapping("/unread")
    public ResponseEntity<Map<String, Object>> getUnreadContacts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Contact> contacts = contactService.getUnreadContacts(pageable);
        
        Map<String, Object> response = new HashMap<>();
        response.put("content", contacts.getContent());
        response.put("totalElements", contacts.getTotalElements());
        response.put("totalPages", contacts.getTotalPages());
        response.put("currentPage", contacts.getNumber());
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/read")
    public ResponseEntity<Map<String, Object>> getReadContacts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Contact> contacts = contactService.getReadContacts(pageable);
        
        Map<String, Object> response = new HashMap<>();
        response.put("content", contacts.getContent());
        response.put("totalElements", contacts.getTotalElements());
        response.put("totalPages", contacts.getTotalPages());
        response.put("currentPage", contacts.getNumber());
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/unread/count")
    public ResponseEntity<Map<String, Long>> getUnreadCount() {
        Map<String, Long> response = new HashMap<>();
        response.put("count", contactService.getUnreadCount());
        return ResponseEntity.ok(response);
    }
    
    @PatchMapping("/{id}/read")
    public ResponseEntity<Contact> markAsRead(@PathVariable Long id) {
        Contact contact = contactService.markAsRead(id);
        return ResponseEntity.ok(contact);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteContact(@PathVariable Long id) {
        contactService.deleteContact(id);
        return ResponseEntity.noContent().build();
    }
}