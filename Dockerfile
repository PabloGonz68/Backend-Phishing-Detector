# Etapa 1: Construcción (Usamos Maven para compilar tu código)
FROM maven:3.9.6-eclipse-temurin-22 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Etapa 2: Producción (Imagen ligera solo con Java para ahorrar memoria)
FROM eclipse-temurin:22-jre-alpine
WORKDIR /app
# Copiamos el archivo .jar que se fabricó en la etapa 1
COPY --from=build /app/target/*.jar app.jar
# Exponemos el puerto de Spring Boot
EXPOSE 8081
# Comando para arrancar el servidor
ENTRYPOINT ["java", "-jar", "app.jar"]