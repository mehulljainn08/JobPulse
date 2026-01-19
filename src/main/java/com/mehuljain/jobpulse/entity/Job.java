package com.mehuljain.jobpulse.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;

@Entity
@Data
@Table(
        name = "jobs",
        uniqueConstraints = @UniqueConstraint(name = "uk_job_hash", columnNames = "jobHash")
)
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID jobId;

    @Column(nullable = false)
    private String companyName;

    @Column(nullable = false)
    private String jobTitle;

    private String location;
    private String applyUrl;

    @Column(columnDefinition = "TEXT")
    private String jobDescription;

    @Column(nullable = false)
    private String source;

    // Lifecycle Management
    @Column(nullable = false)
    private Instant firstSeenAt;

    @Column(nullable = false)
    private Instant lastSeenAt;

    @Column(nullable = false)
    private boolean active = true;


    @Column(name = "job_hash", unique = true)
    private String jobHash;


    @Column(columnDefinition = "TEXT")
    private String aiSummary;

    @ElementCollection
    @CollectionTable(name = "job_ai_tags", joinColumns = @JoinColumn(name = "job_id"))
    @Column(name = "tag")
    private Set<String> aiTags;


    @PrePersist
    public void prePersist() {
        Instant now = Instant.now();
        if (this.firstSeenAt == null) this.firstSeenAt = now;
        this.lastSeenAt = now;
    }
}