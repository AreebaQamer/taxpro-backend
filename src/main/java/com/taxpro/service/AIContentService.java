package com.taxpro.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.taxpro.dto.ContentRequestDTO;
import com.taxpro.dto.ContentResponseDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AIContentService {

    @Value("${gemini.api.key:}")
    private String geminiApiKey;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public ContentResponseDTO generateArticle(ContentRequestDTO request) throws Exception {
        String aiJsonResponse = callGeminiForArticle(request);
        return objectMapper.readValue(aiJsonResponse, ContentResponseDTO.class);
    }

    private String callGeminiForArticle(ContentRequestDTO request) throws Exception {
        if (geminiApiKey == null || geminiApiKey.isEmpty()) {
            throw new IllegalStateException("Gemini API key not found! Please set GEMINI_API_KEY in application.properties");
        }

        GenerativeModel model = new GenerativeModel("gemini-2.0-flash-exp", geminiApiKey);
        GenerativeModelFutures modelFutures = GenerativeModelFutures.from(model);

        String serviceName = request.getServiceName();
        String category = request.getCategory();

        String prompt = String.format(
            "You are a professional content writer for S.Qamer & Co., a top tax consultancy firm in Pakistan with 20+ years of experience.\n\n" +
            "Write a detailed, informative, and client-friendly article about: '%s' in the category '%s'.\n\n" +
            "The article should be written for Pakistani clients who are looking for professional tax services.\n\n" +
            "Return ONLY valid JSON with these exact keys (no markdown, no extra text):\n" +
            "{\n" +
            "  \"title\": \"An engaging, SEO-friendly title for this service (max 60 characters)\",\n" +
            "  \"metaDescription\": \"A 155-160 character meta description for SEO\",\n" +
            "  \"overview\": \"A 3-4 sentence introduction explaining what this service is and why it's important\",\n" +
            "  \"whoIsItFor\": \"A detailed 2-3 sentence paragraph explaining who specifically needs this service (e.g., Freelancers, Salaried Employees, Business Owners)\",\n" +
            "  \"keyBenefits\": [\n" +
            "    \"Benefit 1 with brief explanation (e.g., 'Legal Compliance: Stay compliant with FBR regulations')\",\n" +
            "    \"Benefit 2 with brief explanation\",\n" +
            "    \"Benefit 3 with brief explanation\",\n" +
            "    \"Benefit 4 with brief explanation\"\n" +
            "  ],\n" +
            "  \"requiredDocs\": \"A comprehensive paragraph listing all documents needed for this service\",\n" +
            "  \"processSummary\": \"A step-by-step paragraph explaining how S.Qamer & Co. handles this service (4-5 sentences)\",\n" +
            "  \"faq\": [\n" +
            "    {\"question\": \"Frequently asked question 1?\", \"answer\": \"Detailed answer with 2-3 sentences\"},\n" +
            "    {\"question\": \"Frequently asked question 2?\", \"answer\": \"Detailed answer with 2-3 sentences\"},\n" +
            "    {\"question\": \"Frequently asked question 3?\", \"answer\": \"Detailed answer with 2-3 sentences\"}\n" +
            "  ],\n" +
            "  \"conclusion\": \"A 2-3 sentence closing paragraph that encourages the reader to contact S.Qamer & Co. for expert assistance\"\n" +
            "}\n\n" +
            "Important: Make it relevant to Pakistani tax laws and FBR procedures. Use examples like NTN registration, FBR IRIS portal, etc.\n" +
            "Keep the tone professional, trustworthy, and easy to understand for someone with no tax background.",
            serviceName, category
        );

        Content content = new Content.Builder()
            .addText(prompt)
            .build();

        GenerateContentResponse response = modelFutures.generateContent(content).get();
        String aiResponse = response.getText();
        
        // Clean markdown if present
        aiResponse = aiResponse.replace("```json", "").replace("```", "").trim();

        return aiResponse;
    }
}