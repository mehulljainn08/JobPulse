package com.mehuljain.jobpulse.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "connections",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_user_connection_hash",
                        columnNames = {"user_id", "connectionHash"}
                )
        },
        indexes = {
                @Index(name = "idx_connection_user", columnList = "user_id"),
                @Index(name = "idx_connection_company_canonical", columnList = "companyCanonical")
        }
)
@Data
@NoArgsConstructor
public class Connection {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // Owner of this connection (the user who uploaded/imported it)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false)
    private String companyName;

    // Normalized company name for reliable matching (e.g., "Google India Pvt Ltd" -> "google")
    @Column(nullable = false)
    private String companyCanonical;

    private String title;

    @Email
    private String email;

    private String linkedinUrl;

    // Used to prevent duplicate connections across repeated CSV uploads
    @Column(nullable = false)
    private String connectionHash;

    // Lifecycle for refreshable CSV imports
    @Column(nullable = false)
    private Instant firstImportedAt;

    @Column(nullable = false)
    private Instant lastImportedAt;

    @Column(nullable = false)
    private boolean active;

    @PrePersist
    public void prePersist() {
        Instant now = Instant.now();
        this.firstImportedAt = now;
        this.lastImportedAt = now;
        this.active = true;
    }
}
