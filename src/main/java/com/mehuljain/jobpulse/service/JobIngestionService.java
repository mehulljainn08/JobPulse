package com.mehuljain.jobpulse.service;

import com.mehuljain.jobpulse.entity.Job;
import com.mehuljain.jobpulse.event.JobSavedEvent;
import com.mehuljain.jobpulse.repository.JobRepository;
import com.mehuljain.jobpulse.scraper.JobScraper;
import jakarta.transaction.Transactional;
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
    @Scheduled(cron="0 0 */4 * * *")// every 4 hours
    public void runIngestion(){
        log.info("Starting Job Ingestion service");

        for(JobScraper scraper:scrapers){

            try{
                List<Job> jobs= scraper.getJobs();

                for(Job job:jobs){
                    processJob(job);
                }
            }catch (Exception e){
                log.error("Error during job scraping from {}: {}", scraper.getClass().getSimpleName(), e.getMessage());
            }
        }


        log.info("Finished Job Ingestion service");
    }


    private boolean processJob(Job job) {
        String rawKey = (job.getJobTitle() + job.getCompanyName() + job.getLocation()).toLowerCase();
        String hash = DigestUtils.sha256Hex(rawKey);
        job.setJobHash(hash);


        if (jobRepository.existsByJobHash(hash)) {
            return false;
        }

        try {

            try{
                Thread.sleep(5000); // 1 second delay between AI calls
            }catch (InterruptedException e){}
            String aiSummary = aiService.analyzeJob(job.getJobTitle(), job.getCompanyName(), job.getJobDescription());
            job.setAiSummary(aiSummary);
            Job savedJob = jobRepository.save(job);

            eventPublisher.publishEvent(new JobSavedEvent(savedJob));
            return true;

        } catch (DataIntegrityViolationException e) {
            log.warn("Duplicate job caught by DB: {}", job.getJobTitle());
            return false;
        }
    }


}
