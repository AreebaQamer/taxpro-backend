package com.taxpro.controller;

import com.taxpro.entity.Contact;
import com.taxpro.service.ContactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/contact")
@CrossOrigin(origins = {"http://localhost:3000", "https://sqamer.com", "https://www.sqamer.com"}, allowCredentials = "true")
public class ContactController {

    @Autowired
    private ContactService contactService;

    // Get all contacts with pagination
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllContacts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Contact> contactsPage = contactService.getAllContacts(pageable);
        
        Map<String, Object> response = new HashMap<>();
        response.put("content", contactsPage.getContent());
        response.put("totalElements", contactsPage.getTotalElements());
        response.put("totalPages", contactsPage.getTotalPages());
        response.put("currentPage", contactsPage.getNumber());
        
        return ResponseEntity.ok(response);
    }

    // Create new contact
    @PostMapping
    public ResponseEntity<Contact> createContact(@RequestBody Contact contact) {
        Contact savedContact = contactService.saveContact(contact);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedContact);
    }

    // Get single contact by ID
    @GetMapping("/{id}")
    public ResponseEntity<Contact> getContactById(@PathVariable Long id) {
        Contact contact = contactService.getContactById(id);
        return ResponseEntity.ok(contact);
    }

    // Delete contact
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteContact(@PathVariable Long id) {
        contactService.deleteContact(id);
        return ResponseEntity.ok("Contact deleted successfully!");
    }
}