# Phishing Analyzer API 🛡️🤖

[![Spring Boot](https://img.shields.io/badge/Spring__Boot-3.x-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)](https://spring.io/projects/spring-boot)
[![Java 22](https://img.shields.io/badge/Java-22-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://openjdk.org/)
[![Docker](https://img.shields.io/badge/Docker-Enabled-2496ED?style=for-the-badge&logo=docker&logoColor=white)](https://www.docker.com/)
[![DevSecOps](https://img.shields.io/badge/Security-Zero__Trust-red?style=for-the-badge)](https://en.wikipedia.org/wiki/DevSecOps)

API REST stateless de alto rendimiento diseñada bajo principios de **DevSecOps** y **Zero Trust**. El sistema automatiza el análisis forense de correos electrónicos sospechosos utilizando Inteligencia Artificial (Llama 3.1 vía Groq Cloud), protegiendo la infraestructura contra abusos de cuota mediante un sistema avanzado de **Rate Limiting** por IP.

---

## 🚀 Características Clave & Arquitectura

- **Análisis de IA Estructurado (JSON Inmutable):** Integración directa con LLMs de última generación (`llama-3.1-8b-instant`) mediante *Prompt Engineering Defensivo*, garantizando respuestas en formato JSON determinista sin riesgo de alucinaciones o desalineación de tipos de datos.
- **Protección Anti-Abuso (Rate Limiting):** Implementación del algoritmo **Token Bucket** mediante `Bucket4j`. Asigna cubos de tokens virtuales dinámicos por dirección IP (almacenados concurrentemente en memoria RAM), mitigando ataques de Denegación de Servicio (DoS) y controlando de forma estricta los costes de facturación de APIs de terceros. Devuelve el estado HTTP estándar `429 Too Many Requests`.
- **Estrategia de Seguridad Robusta:** Configuración de `Spring Security` en modo puramente *Stateless* (sin estado), inhabilitando la superficie de ataque CSRF (innecesaria en APIs M2M que no exponen cookies) y securizando las fronteras del backend mediante políticas estrictas de control de origen (**CORS**).
- **Persistencia Trazable:** Persistencia asíncrona de auditorías utilizando `Spring Data JPA` sobre una base de datos embebida `H2`, gestionando identificadores únicos globales mediante `UUID` y sellos de tiempo ISO.

---

## 🛠️ Stack Tecnológico

| Categoría | Tecnología |
|---|---|
| Core Framework | Spring Boot 4.1.0 / Java 22 |
| Seguridad | Spring Security 7.x |
| Resiliencia & Rate Limit | Bucket4j Core 8.10.1 |
| Persistencia & Datos | Spring Data JPA, Hibernate 7.x, H2 Database |
| Mapeo y Serialización | Jackson Databind |
| Contenerización | Docker (Multi-stage build) |

---

## 🛑 Justificaciones de Diseño (DevSecOps Mindset)

### ¿Por qué Rate Limiting por IP en lugar de API Keys en el Frontend?

Exponer una API Key estática en una aplicación de cliente (React/Astro) permite que cualquier actor malicioso extraiga la clave desde las herramientas de desarrollador del navegador (F12). Al implementar un filtro de control de tasa basado en el ciclo `OncePerRequestFilter` en el backend, protegemos la cuota financiera del proveedor de IA de manera transparente y segura, sin fricciones para el usuario legítimo.

### Uso de ConcurrentHashMap para Concurrencia Segura

El mapeo de IPs a cubos se gestiona mediante un `ConcurrentHashMap`. En entornos web multihilo, el uso de colecciones no sincronizadas produce condiciones de carrera (*Race Conditions*). Esta estructura garantiza operaciones atómicas a nivel de celda de memoria, soportando picos de tráfico concurrentes sin degradación de rendimiento.

---

## 🛣️ API Endpoints

### Analizar Correo Electrónico

Analiza el asunto y el cuerpo de un correo electrónico para detectar tácticas de ingeniería social, enlaces maliciosos y nivel de riesgo.

- **URL:** `/api/v1/phishing/analyze`
- **Método:** `POST`
- **Headers:** `Content-Type: application/json`

**Cuerpo de la petición (Request Body):**

```json
{
  "subject": "URGENTE: Su cuenta ha sido bloqueada por actividad inusual",
  "body": "Estimado cliente, hemos detectado accesos no autorizados en su cuenta. Por favor haga clic en el siguiente enlace: http://banco-seguro-login-update.com/auth"
}
```

**Respuesta exitosa (200 OK):**

```json
{
  "id": "8f1f8433-19fd-4b8e-aab6-217644228a3f",
  "emailSubject": "URGENTE: Su cuenta ha sido bloqueada por actividad inusual",
  "emailBody": "Estimado cliente, hemos detectado accesos no autorizados en su cuenta...",
  "threatScore": 8,
  "maliciousLinks": "http://banco-seguro-login-update.com/auth",
  "tacticsUsed": "Phishing, Sentido de Urgencia, Suplantación de Identidad",
  "analyzedAt": "2026-07-04T13:26:21.341628"
}
```

**Respuesta por límite excedido (429 Too Many Requests):**

```json
{
  "error": "Límite de peticiones excedido. Inténtalo de nuevo en 1 hora."
}
```

---

## ⚙️ Instalación y Despliegue Local

### Requisitos Previos

- JDK 22 instalado.
- Variable de entorno `AI_API_KEY` (tu clave de Groq Cloud) configurada en tu sistema.

### Ejecución

1. Clonar el repositorio.
2. Configurar las variables de entorno o agregarlas en un archivo `.env`.
3. Compilar y arrancar la aplicación con Maven:

```bash
mvn clean spring-boot:run
```

La API estará disponible de forma nativa en `http://localhost:8081`.
