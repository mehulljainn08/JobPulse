package com.mehuljain.jobpulse.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "job_matches",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_user_job_connection",
                        columnNames = {"user_id", "job_id", "connection_id"}
                )
        },
        indexes = {
                @Index(name = "idx_job_match_user", columnList = "user_id"),
                @Index(name = "idx_job_match_job", columnList = "job_id"),
                @Index(name = "idx_job_match_connection", columnList = "connection_id")
        }
)
@Data
@NoArgsConstructor
public class JobMatch {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // The user for whom this match exists
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // The job that was found
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "job_id", nullable = false)
    private Job job;

    // The matching connection (someone the user knows in that company)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "connection_id", nullable = false)
    private Connection connection;

    // Match confidence score (company similarity etc.)
    @Column(nullable = false)
    private Integer matchScore;

    @Column(nullable = false)
    private Instant matchedAt;

    @PrePersist
    public void prePersist() {
        this.matchedAt = Instant.now();
    }
}
