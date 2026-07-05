package com.pgs.phising_detector.repository;

import com.pgs.phising_detector.model.PhishingAnalysis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PhishingAnalysisRepository extends JpaRepository<PhishingAnalysis, UUID> {
}
