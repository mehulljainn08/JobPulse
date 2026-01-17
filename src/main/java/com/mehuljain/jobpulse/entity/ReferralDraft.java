package com.mehuljain.jobpulse.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "outreach_drafts",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_user_jobmatch_draft",
                        columnNames = {"user_id", "job_match_id"}
                )
        },
        indexes = {
                @Index(name = "idx_outreach_user", columnList = "user_id"),
                @Index(name = "idx_outreach_job_match", columnList = "job_match_id"),
                @Index(name = "idx_outreach_status", columnList = "status")
        }
)
@Data
@NoArgsConstructor
public class ReferralDraft {

    public enum Status {
        DRAFT,
        SENT,
        DISMISSED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // Owner of the draft
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Draft is created for a specific match (job + connection)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "job_match_id", nullable = false)
    private JobMatch jobMatch;

    @Column(nullable = false)
    private String subject;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String messageBody;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @Column(nullable = false)
    private Instant createdAt;

    private Instant updatedAt;

    @PrePersist
    public void prePersist() {
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
        this.status = Status.DRAFT;
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = Instant.now();
    }
}
