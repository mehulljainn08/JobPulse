package com.mehuljain.jobpulse.config;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiConfig {

    @Value("${langchain4j.google-ai-gemini.chat-model.api-key}") // Reading from your properties
    private String googleApiKey;

    @Bean
    public ChatLanguageModel chatLanguageModel() {
        return GoogleAiGeminiChatModel.builder()
                .apiKey(googleApiKey)
                .modelName("gemini-2.5-flash") // Or "gemini-pro"
                .temperature(0.7)
                .build();
    }
}