package com.taxpro.repository;

import com.taxpro.entity.Contact;
import org.springframework.data.domain.Page;        // ✅ ADD THIS
import org.springframework.data.domain.Pageable;  // ✅ ADD THIS
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ContactRepository extends JpaRepository<Contact, Long> {
    
    // Find by service type
    List<Contact> findByServiceType(String serviceType);
    
    // Find by email
    List<Contact> findByEmail(String email);
    
    
}