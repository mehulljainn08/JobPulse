package com.mehuljain.jobpulse.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "users") // 'users' is good, '_user' is safer in Postgres but 'users' works usually
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User implements UserDetails { // 👈 Implements UserDetails is CRITICAL

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;


    @Column(nullable = false)
    private String password; // Hashed password

    @Enumerated(EnumType.STRING)
    private Role role;

    @Enumerated(EnumType.STRING)
    private AuthProvider provider;


    private String username; // Display name

    @Email
    @Column(nullable = false, unique = true)
    private String email;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_desired_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role")
    private Set<String> desiredRoles;

    @ElementCollection
    @CollectionTable(name = "user_skills", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "skill")
    private Set<String> skills;

    private String resumeUrl;
    private int experienceInYears;
    private String linkedinProfile;

    @Column(nullable = false)
    private Instant createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = Instant.now();
    }



    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() { return true; }
    @Override
    public boolean isAccountNonLocked() { return true; }
    @Override
    public boolean isCredentialsNonExpired() { return true; }
    @Override
    public boolean isEnabled() { return true; }
}