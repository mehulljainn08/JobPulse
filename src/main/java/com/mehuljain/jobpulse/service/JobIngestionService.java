package com.mehuljain.jobpulse.service;

import com.mehuljain.jobpulse.dto.JobInsight;
import com.mehuljain.jobpulse.entity.Job;
import com.mehuljain.jobpulse.event.JobSavedEvent;
import com.mehuljain.jobpulse.repository.JobRepository;
import com.mehuljain.jobpulse.scraper.JobScraper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class JobIngestionService {

    private final JobRepository jobRepository;
    private final List<JobScraper> scrapers;
    private final ApplicationEventPublisher eventPublisher;
    private final AIService aiService;

    @EventListener(ApplicationReadyEvent.class)
    @Scheduled(cron="0 0 */4 * * *")
    public void runIngestion(){
        log.info("🚀 Starting Job Ingestion...");

        for(JobScraper scraper : scrapers){
            try {
                List<Job> jobs = scraper.getJobs();
                for(Job job : jobs){
                    processJob(job);
                }
            } catch (Exception e){
                log.error("Scraper Error {}: {}", scraper.getClass().getSimpleName(), e.getMessage());
            }
        }
        log.info("Job Ingestion Finished.");
    }

    private boolean processJob(Job job) {
        String rawKey = (job.getJobTitle() + job.getCompanyName() + job.getLocation()).toLowerCase();
        String hash = DigestUtils.sha256Hex(rawKey);
        job.setJobHash(hash);

        if (jobRepository.existsByJobHash(hash)) {
            return false;
        }

        try {
            // 5 seconds for rate limits
            try { Thread.sleep(5000); } catch (InterruptedException e) {}


            String fullDescription = String.format("Title: %s\nCompany: %s\nDescription: %s",
                    job.getJobTitle(), job.getCompanyName(), job.getJobDescription());


            try {
                log.info("Analyzing: {}...", job.getJobTitle());
                JobInsight insight = aiService.analyzeJob(fullDescription);


                job.setAiSummary(insight.summary());
                job.setTechStack(insight.techStack());
                job.setExperienceLevel(insight.experienceLevel());
                job.setRemote(insight.isRemote());
                job.setSalaryRange(insight.salaryRange());

            } catch (Exception e) {
                log.warn("⚠️ AI Failed for '{}': {}. Saving without AI data.", job.getJobTitle(), e.getMessage());
                job.setAiSummary("Analysis Pending");
                job.setTechStack(List.of("N/A"));
            }


            Job savedJob = jobRepository.save(job);
            eventPublisher.publishEvent(new JobSavedEvent(savedJob));
            return true;

        } catch (DataIntegrityViolationException e) {
            return false;
        }
    }



}