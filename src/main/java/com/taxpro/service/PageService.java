// com/example/portal/service/PageService.java
package com.taxpro.service;

import com.taxpro.entity.PageContent;
import com.taxpro.repository.PageRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class PageService {
    
    @Autowired
    private PageRepository pageRepository;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    // Get all pages structure
    public Map<String, List<Map<String, String>>> getPagesStructure() {
        Map<String, List<Map<String, String>>> structure = new LinkedHashMap<>();
        
        // Corporate Pages
        List<Map<String, String>> corporate = new ArrayList<>();
        corporate.add(createPageInfo("chamber-of-commerce", "Chamber of Commerce"));
        corporate.add(createPageInfo("company-registration", "Company Registration"));
        corporate.add(createPageInfo("firm-registration", "Firm Registration"));
        corporate.add(createPageInfo("ngo-registration", "NGO Registration"));
        corporate.add(createPageInfo("pec-registration", "PEC Registration"));
        corporate.add(createPageInfo("secp-matters", "SECP Matters"));
        corporate.add(createPageInfo("software-house-registration", "Software House Registration"));
        structure.put("corporate", corporate);
        
        // Immigration Pages
        List<Map<String, String>> immigration = new ArrayList<>();
        immigration.add(createPageInfo("business-immigration", "Business Immigration"));
        immigration.add(createPageInfo("skilled-immigration", "Skilled Immigration"));
        immigration.add(createPageInfo("study-visa", "Study Visa"));
        immigration.add(createPageInfo("visa-appeals", "Visa Appeals"));
        immigration.add(createPageInfo("visa-file-preparation", "Visa File Preparation"));
        structure.put("immigration", immigration);
        
        // Legal Pages
        List<Map<String, String>> legal = new ArrayList<>();
        legal.add(createPageInfo("banking-case", "Banking Case"));
        legal.add(createPageInfo("civil-case", "Civil Case"));
        legal.add(createPageInfo("criminal-case", "Criminal Case"));
        legal.add(createPageInfo("family-case", "Family Case"));
        legal.add(createPageInfo("fia-case", "FIA Case"));
        legal.add(createPageInfo("property-case", "Property Case"));
        structure.put("legal", legal);
        
        // Tax Pages
        List<Map<String, String>> tax = new ArrayList<>();
        tax.add(createPageInfo("active-tax-filer", "Active Tax Filer"));
        tax.add(createPageInfo("income-tax-return", "Income Tax Return"));
        tax.add(createPageInfo("ntn-registration", "NTN Registration"));
        tax.add(createPageInfo("sales-tax-return", "Sales Tax Return"));
        tax.add(createPageInfo("tax-appeals", "Tax Appeals"));
        tax.add(createPageInfo("tax-refund", "Tax Refund"));
        structure.put("tax", tax);
        
        // News Pages
        List<Map<String, String>> news = new ArrayList<>();
        news.add(createPageInfo("all-news", "All News"));
        news.add(createPageInfo("news-detail", "News Detail"));
        structure.put("news", news);
        
        // Other Pages
        List<Map<String, String>> other = new ArrayList<>();
        other.add(createPageInfo("home", "Home"));
        other.add(createPageInfo("about", "About"));
        other.add(createPageInfo("contact", "Contact"));
        other.add(createPageInfo("dynamic-page", "Dynamic Page"));
        structure.put("other", other);
        
        return structure;
    }
    
    private Map<String, String> createPageInfo(String id, String name) {
        Map<String, String> info = new HashMap<>();
        info.put("id", id);
        info.put("name", name);
        return info;
    }
    
    // Get single page
    public PageContent getPage(String pageId) {
        Optional<PageContent> existing = pageRepository.findByPageId(pageId);
        if (existing.isPresent()) {
            return existing.get();
        } else {
            return createDefaultPage(pageId);
        }
    }
    
    // Create default page
    private PageContent createDefaultPage(String pageId) {
        PageContent page = new PageContent();
        page.setPageId(pageId);
        page.setTitle(formatTitle(pageId));
        page.setCategory(getCategory(pageId));
        page.setVersion(1);
        
        // Default Content JSON
        Map<String, Object> defaultContent = new HashMap<>();
        
        // Hero Section
        Map<String, String> hero = new HashMap<>();
        hero.put("heading", "Welcome to " + formatTitle(pageId));
        hero.put("subheading", "Your trusted partner for professional services");
        hero.put("description", "Edit this content from admin panel. You can change everything here.");
        defaultContent.put("hero", hero);
        
        // Sections
        List<Map<String, String>> sections = new ArrayList<>();
        Map<String, String> section1 = new HashMap<>();
        section1.put("title", "About Our Service");
        section1.put("content", "We provide expert guidance and support for all your needs.");
        sections.add(section1);
        
        Map<String, String> section2 = new HashMap<>();
        section2.put("title", "Why Choose Us");
        section2.put("content", "Professional team, Fast processing, Affordable rates, 24/7 support");
        sections.add(section2);
        defaultContent.put("sections", sections);
        
        // FAQ
        List<Map<String, String>> faq = new ArrayList<>();
        Map<String, String> faq1 = new HashMap<>();
        faq1.put("question", "How can I get started?");
        faq1.put("answer", "Contact us through our contact form or call us directly.");
        faq.add(faq1);
        
        Map<String, String> faq2 = new HashMap<>();
        faq2.put("question", "What documents are required?");
        faq2.put("answer", "Required documents include CNIC, proof of address, and relevant certificates.");
        faq.add(faq2);
        defaultContent.put("faq", faq);
        
        // CTA
        Map<String, String> cta = new HashMap<>();
        cta.put("text", "Contact Us Today");
        cta.put("link", "/contact");
        cta.put("buttonColor", "#3b82f6");
        defaultContent.put("cta", cta);
        
        try {
            page.setContent(objectMapper.writeValueAsString(defaultContent));
        } catch(Exception e) {
            page.setContent("{}");
        }
        
        // Default Meta Data
        Map<String, String> defaultMeta = new HashMap<>();
        defaultMeta.put("description", "Professional services for " + formatTitle(pageId));
        defaultMeta.put("keywords", formatTitle(pageId) + ", services, registration");
        
        try {
            page.setMetaData(objectMapper.writeValueAsString(defaultMeta));
        } catch(Exception e) {
            page.setMetaData("{}");
        }
        
        return page;
    }
    
    private String formatTitle(String pageId) {
        String[] words = pageId.split("-");
        StringBuilder title = new StringBuilder();
        for (String word : words) {
            title.append(word.substring(0, 1).toUpperCase())
                 .append(word.substring(1))
                 .append(" ");
        }
        return title.toString().trim();
    }
    
    private String getCategory(String pageId) {
        if (pageId.contains("chamber") || pageId.contains("registration") || pageId.contains("secp"))
            return "corporate";
        if (pageId.contains("visa") || pageId.contains("immigration"))
            return "immigration";
        if (pageId.contains("case"))
            return "legal";
        if (pageId.contains("tax") || pageId.contains("filer"))
            return "tax";
        if (pageId.contains("news"))
            return "news";
        return "other";
    }
    
    // Update page
    public PageContent updatePage(String pageId, PageContent updatedPage, String username) {
        Optional<PageContent> existing = pageRepository.findByPageId(pageId);
        PageContent page;
        
        if (existing.isPresent()) {
            page = existing.get();
            page.setTitle(updatedPage.getTitle());
            page.setContent(updatedPage.getContent());
            page.setMetaData(updatedPage.getMetaData());
            page.setVersion(page.getVersion() + 1);
        } else {
            page = updatedPage;
            page.setPageId(pageId);
            page.setVersion(1);
        }
        
        page.setUpdatedBy(username);
        page.setUpdatedAt(LocalDateTime.now());
        
        return pageRepository.save(page);
    }
}