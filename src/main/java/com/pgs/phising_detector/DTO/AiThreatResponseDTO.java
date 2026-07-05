package com.pgs.phising_detector.DTO;

public record AiThreatResponseDTO(
        Integer threatScore,
        String maliciousLinks,
        String tacticsUsed
) {}

