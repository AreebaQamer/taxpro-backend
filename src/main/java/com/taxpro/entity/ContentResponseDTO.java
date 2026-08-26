package com.taxpro.entity;

import lombok.Data;
import java.util.List;

@Data
public class ContentResponseDTO {
    private String title;
    private String metaDescription;
    private String overview;
    private String whoIsItFor;
    private List<String> keyBenefits;
    private String requiredDocs;
    private String processSummary;
    private List<FaqItem> faq;
    private String conclusion;

    @Data
    public static class FaqItem {
        private String question;
        private String answer;
    }
}