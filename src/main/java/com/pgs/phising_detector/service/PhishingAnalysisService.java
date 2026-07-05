package com.pgs.phising_detector.service;

import com.pgs.phising_detector.DTO.AiThreatResponseDTO;
import com.pgs.phising_detector.DTO.PhishingRequestDTO;
import com.pgs.phising_detector.model.PhishingAnalysis;
import com.pgs.phising_detector.repository.PhishingAnalysisRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class PhishingAnalysisService {

    private final RestClient aiRestClient;
    private final PhishingAnalysisRepository repository;
    private final ObjectMapper objectMapper;

    public PhishingAnalysis analyzeEmail (PhishingRequestDTO request){
        log.info("Iniciando analisis de phishing para el asunto: {}", request.subject());

        // 1. Construir el prompt estricto (Añadimos reglas anti-arrays)
        String prompt = """
                Actúa como un experto en ciberseguridad y analiza el siguiente correo electrónico.
                Devuelve la respuesta ESTRICTAMENTE en formato JSON válido con esta estructura exacta:
                {
                  "threatScore": (un entero del 1 al 10 indicando la peligrosidad),
                  "maliciousLinks": (un único texto con enlaces separados por comas, o "Ninguno"),
                  "tacticsUsed": (un ÚNICO texto de String con las tácticas separadas por comas. PROHIBIDO USAR ARRAYS [])
                }
                
                Correo a analizar:
                Asunto: %s
                Cuerpo: %s
                """.formatted(request.subject(), request.body());

        Map<String, Object> requestBody = Map.of(
                "model", "llama-3.1-8b-instant", // O el modelo gratuito que elijas de Groq/Gemini
                "messages", new Object[]{
                        Map.of("role", "user", "content", prompt)
                },
                "response_format", Map.of("type", "json_object") // Forzamos modo JSON si la API lo soporta
        );

        try{
            String rawResponse = aiRestClient.post()
                    .uri("/chat/completions")
                    .body(requestBody)
                    .retrieve()
                    .body(String.class);

            JsonNode rootNode = objectMapper.readTree(rawResponse);
            String contentString = rootNode.path("choices").get(0).path("message").path("content").asText();

            AiThreatResponseDTO aiResponse = objectMapper.readValue(contentString, AiThreatResponseDTO.class);
            if (aiResponse == null){
                throw new RuntimeException("La API de IA devolvió una respuesta vacía");
            }

            PhishingAnalysis analysis = PhishingAnalysis.builder()
                    .emailSubject(request.subject())
                    .emailBody(request.body())
                    .threatScore(aiResponse.threatScore())
                    .maliciousLinks(aiResponse.maliciousLinks())
                    .tacticsUsed(aiResponse.tacticsUsed())
                    .build();

            PhishingAnalysis savedAnalysis = repository.save(analysis);
            log.info("Análisis completado con exito. ID generado: {}", savedAnalysis.getId());

            return savedAnalysis;

        } catch (Exception e) {
            log.error("Error critico durante el analisis del correo: {}", e.getMessage());
            throw new RuntimeException("Error al comunicarse con el proveedor de IA para el analisis de seguridad", e);
        }

    }


}
