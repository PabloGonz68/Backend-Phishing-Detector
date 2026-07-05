package com.pgs.phising_detector.DTO;

import jakarta.validation.constraints.NotBlank;

public record PhishingRequestDTO(
        @NotBlank(message = "El asunto del correo es obligatorio")
        String subject,

        @NotBlank(message = "El cuerpo del correo es obligatorio")
        String body
) {}
