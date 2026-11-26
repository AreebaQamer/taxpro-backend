package com.taxpro.repository;

import com.taxpro.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    
    // URL slug (jaise "business" ya "tax-matters") ke zariye category find karne ke liye
    Optional<Category> findBySlug(String slug);
    
}
