package com.mehuljain.jobpulse.dto;

import java.util.List;




public record JobInsight(
        String summary,
        List<String> techStack,
        String experienceLevel,
        boolean isRemote,
        String salaryRange        
) {}