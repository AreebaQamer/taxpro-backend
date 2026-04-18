package com.taxpro.service;

import com.taxpro.entity.Contact;
import com.taxpro.repository.ContactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ContactService {
    
    @Autowired
    private ContactRepository contactRepository;
    
    // ================================================
    // BASIC METHODS
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
    // METHODS WITH PAGINATION (Called by AdminContactController)
    // ================================================
    
    public Page<Contact> getAllContacts(Pageable pageable) {
        return contactRepository.findAll(pageable);
    }
    
    public Page<Contact> getUnreadContacts(Pageable pageable) {
        return contactRepository.findByIsReadFalse(pageable);
    }
    
    public Page<Contact> getReadContacts(Pageable pageable) {
        return contactRepository.findByIsReadTrue(pageable);
    }
    
    public long getUnreadCount() {
        return contactRepository.countByIsReadFalse();
    }
    
    public Contact markAsRead(Long id) {
        Contact contact = contactRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Contact not found with id: " + id));
        contact.setRead(true);
        return contactRepository.save(contact);
    }
}