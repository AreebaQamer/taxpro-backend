package com.taxpro.service;

import com.taxpro.entity.Contact;
import com.taxpro.repository.ContactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;        // ✅ ADD THIS
import org.springframework.data.domain.Pageable;  // ✅ ADD THIS
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ContactService {
    
    @Autowired
    private ContactRepository contactRepository;
    
    // ================================================
    // BASIC METHODS (No Pagination)
    // ================================================
    
    public List<Contact> getAllContactsList() {
        return contactRepository.findAll();
    }
    
    public Contact getContactById(Long id) {
        return contactRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Contact not found with id: " + id));
    }
    
    public Contact saveContact(Contact contact) {
        return contactRepository.save(contact);
    }
    
    public void deleteContact(Long id) {
        contactRepository.deleteById(id);
    }
    
    // ================================================
    // METHODS WITH PAGINATION
    // ================================================
    
    public Page<Contact> getAllContacts(Pageable pageable) {
        return contactRepository.findAll(pageable);
    }
    
   
}