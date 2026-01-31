package com.mehuljain.jobpulse.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.Instant;
import java.util.List;
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

    // --- Lifecycle Management ---
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

    private String experienceLevel;

    private boolean isRemote;

    private String salaryRange;

    // We changed Set<String> to List<String> to match the AI logic
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "job_tech_stack", joinColumns = @JoinColumn(name = "job_id"))
    @Column(name = "technology")
    private List<String> techStack;

    // (Optional) You can keep your old tags if you want, otherwise remove this
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