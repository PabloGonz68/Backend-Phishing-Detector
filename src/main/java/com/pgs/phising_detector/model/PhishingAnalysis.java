package com.pgs.phising_detector.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "phishing_analysis")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PhishingAnalysis {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    private UUID id;

    @Column(nullable = false)
    private String emailSubject;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String emailBody;

    @Column(nullable = false)
    private Integer threatScore;

    @Column(columnDefinition = "TEXT")
    private String maliciousLinks;

    @Column(length = 500)
    private String tacticsUsed;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime analyzedAt;

}
