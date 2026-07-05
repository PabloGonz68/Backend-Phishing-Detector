package com.pgs.phising_detector.security;

import com.pgs.phising_detector.service.RateLimitingService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class RateLimitFilter extends OncePerRequestFilter {

    private final RateLimitingService rateLimitingService;

    // Método auxiliar para obtener la IP real detrás del proxy o balanceador de carga (Render)
    private String getClientIP(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");

        if (xfHeader == null || xfHeader.isEmpty()) {
            // Si no hay cabecera (ej. pruebas en local), leemos la IP directa
            return request.getRemoteAddr();
        }

        // Si hay varios proxies, las IPs vienen separadas por comas. Nos quedamos con la primera (la del cliente original).
        return xfHeader.split(",")[0].trim();
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Sustituimos getRemoteAddr() por nuestro nuevo método
        String ip = getClientIP(request);

        io.github.bucket4j.Bucket bucket = rateLimitingService.resolveBucket(ip);

        if (bucket.tryConsume(1)) {
            filterChain.doFilter(request, response);
        } else {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType("application/json");
            // Nota: Mantenemos el mensaje sin tildes para evitar problemas de codificación de texto
            response.getWriter().write("{\"error\": \"Limite de peticiones excedido. Intentalo de nuevo en 1 hora.\"}");
        }
    }
}