package com.taxpro.repository;

import com.taxpro.entity.Contact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ContactRepository extends JpaRepository<Contact, Long> {
    List<Contact> findByServiceType(String serviceType);
    List<Contact> findByEmail(String email);
}