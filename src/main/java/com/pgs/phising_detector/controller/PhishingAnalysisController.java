package com.pgs.phising_detector.controller;

import com.pgs.phising_detector.DTO.PhishingRequestDTO;
import com.pgs.phising_detector.model.PhishingAnalysis;
import com.pgs.phising_detector.service.PhishingAnalysisService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/phishing")
@RequiredArgsConstructor
public class PhishingAnalysisController {

    private final PhishingAnalysisService service;

    @PostMapping("/analyze")
    public ResponseEntity<PhishingAnalysis> analyzeEmail(@Valid @RequestBody PhishingRequestDTO request){

        PhishingAnalysis result = service.analyzeEmail(request);
        return ResponseEntity.ok(result);
    }
}
