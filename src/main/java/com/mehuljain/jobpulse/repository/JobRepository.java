package com.mehuljain.jobpulse.repository;

import com.mehuljain.jobpulse.entity.Job;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface JobRepository extends JpaRepository<Job, UUID> {

    public Boolean existsByJobHash(String jobHash);
}
