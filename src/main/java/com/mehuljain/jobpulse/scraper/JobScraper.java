package com.mehuljain.jobpulse.scraper;

import com.mehuljain.jobpulse.entity.Job;

import java.util.List;

public interface JobScraper {

    public List<Job> getJobs();
}
