package com.taxpro.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taxpro.entity.ContentRequestDTO;
import com.taxpro.entity.ContentResponseDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Service
public class AIContentService {

    @Value("${gemini.api.key:}")
    private String geminiApiKey;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final WebClient webClient = WebClient.create();

    public ContentResponseDTO generateServiceContent(ContentRequestDTO request) throws Exception {
        // 1. Prompt banayein
        String prompt = String.format(
            "You are a professional content writer for S.Qamer & Co., a top tax consultancy firm in Pakistan.\n\n" +
            "Write detailed content for the service: '%s' in the category '%s'.\n\n" +
            "Return ONLY valid JSON with these exact keys (no markdown, no extra text):\n" +
            "{\n" +
            "  \"tag\": \"A short 2-3 word tagline (e.g., 'Tax Registration')\",\n" +
            "  \"title\": \"An engaging, SEO-friendly title for this service (max 60 characters)\",\n" +
            "  \"desc\": \"A 1-2 sentence description that appears in the hero section\",\n" +
            "  \"breadcrumb\": \"Breadcrumb text (e.g., 'Home › Tax Matters › NTN Registration')\",\n" +
            "  \"intro\": \"A 2-3 sentence introduction explaining what this service is and why it's important (HTML with <p> tags)\",\n" +
            "  \"cards\": [\n" +
            "    {\"icon\": \"👤\", \"title\": \"Card Title 1\", \"desc\": \"Brief description\", \"link\": \"/link1\"},\n" +
            "    {\"icon\": \"🏪\", \"title\": \"Card Title 2\", \"desc\": \"Brief description\", \"link\": \"/link2\"}\n" +
            "  ],\n" +
            "  \"steps\": [\"Step 1 description\", \"Step 2 description\", \"Step 3 description\", \"Step 4 description\"],\n" +
            "  \"docs\": [\"Document 1\", \"Document 2\", \"Document 3\"],\n" +
            "  \"faqs\": [\n" +
            "    {\"question\": \"Frequently asked question 1?\", \"answer\": \"Detailed answer with 2-3 sentences\"},\n" +
            "    {\"question\": \"Frequently asked question 2?\", \"answer\": \"Detailed answer with 2-3 sentences\"}\n" +
            "  ],\n" +
            "  \"ctaTitle\": \"Ready to Get Started?\",\n" +
            "  \"ctaSub\": \"Contact our experts today for fast, hassle-free service.\",\n" +
            "  \"sidebarLinks\": [\n" +
            "    {\"label\": \"NTN Registration\", \"href\": \"/tax-registration\"},\n" +
            "    {\"label\": \"Sales Tax Registration\", \"href\": \"/sales-tax-registration\"}\n" +
            "  ],\n" +
            "  \"sidebarCta\": \"Need Help?\",\n" +
            "  \"sidebarDesc\": \"Speak directly with our expert for personalized assistance.\"\n" +
            "}\n\n" +
            "Make it relevant to Pakistani tax laws and FBR procedures. Keep the tone professional and easy to understand.",
            request.getServiceName(), request.getCategory()
        );

        // 2. Gemini API Request Body
        Map<String, Object> requestBody = Map.of(
            "contents", new Object[] {
                Map.of("parts", new Object[] {
                    Map.of("text", prompt)
                })
            }
        );

        // 3. Gemini API Call
        String response = webClient.post()
            .uri("https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash-exp:generateContent?key=" + geminiApiKey)
            .header("Content-Type", "application/json")
            .bodyValue(requestBody)
            .retrieve()
            .bodyToMono(String.class)
            .block();

        // 4. Parse Gemini Response
        Map<String, Object> geminiResponse = objectMapper.readValue(response, Map.class);
        
        // Extract text from Gemini response
        List<Map<String, Object>> candidates = (List<Map<String, Object>>) geminiResponse.get("candidates");
        Map<String, Object> content = (Map<String, Object>) candidates.get(0).get("content");
        List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");
        String generatedText = (String) parts.get(0).get("text");

        // 5. Clean JSON (remove markdown)
        String cleanJson = generatedText
            .replace("```json", "")
            .replace("```", "")
            .trim();

        // 6. Parse to DTO
        return objectMapper.readValue(cleanJson, ContentResponseDTO.class);
    }
}