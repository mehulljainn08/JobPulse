package com.mehuljain.jobpulse.service;

import com.mehuljain.jobpulse.dto.JobInsight; // Make sure you created this Record!
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.spring.AiService;

// We use the Interface approach (The "Magic" way)
// simpler and more powerful than the manual class you wrote.
@AiService
public interface AIService {

    @SystemMessage("""
        You are an expert technical recruiter.
        Analyze the job description and extract structured insights.
        
        Rules:
        1. summary: A 2-sentence professional summary.
        2. techStack: Extract specific technologies (e.g., "Java", "Spring", "AWS"). Max 5 items.
        3. experienceLevel: Infer JUNIOR, MID, SENIOR, or INTERN.
        4. isRemote: true only if explicitly stated.
        5. salaryRange: Extract if present, else "Not Disclosed".
    """)
    JobInsight analyzeJob(@UserMessage String jobDescription);
}