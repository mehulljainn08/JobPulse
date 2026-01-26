package com.mehuljain.jobpulse.service;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AIService {

    // Injecting the key from application.properties
    @Value("${spring.ai.google.genai.api-key}")
    private String googleApiKey;

    private ChatLanguageModel chatModel;

    @PostConstruct
    public void init() {
        // Build the manual instance using your key
        this.chatModel = GoogleAiGeminiChatModel.builder()
                .apiKey(googleApiKey)
                .modelName("gemini-flash-latest")
                .temperature(0.7)
                .build();
    }

    public String analyzeJob(String jobTitle, String company, String rawDescription) {

        // 1. The Prompt: We ask for HTML so it fits in the email table
        String prompt = String.format("""
            Analyze this job description for a software role.
            
            Job: %s at %s
            Description: %s
            
            Instructions:
            1. Summarize the role in 2 sentences.
            2. List the key Tech Stack.
            3. Return ONLY a valid HTML <ul> list.
            4. Format:
               <ul>
                 <li><b>Summary:</b> [Your summary]</li>
                 <li><b>Tech Stack:</b> [Java, React, etc]</li>
               </ul>
            """, jobTitle, company, rawDescription);

        try {

            String response = chatModel.generate(prompt);
            return response;

        } catch (Exception e) {
            log.error("AI Error for {}: {}", jobTitle, e.getMessage());
            return "<ul><li><b>Status:</b> AI Summary Unavailable</li></ul>";
        }
    }
}