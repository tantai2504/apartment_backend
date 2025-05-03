# syntax=docker/dockerfile:1.4

# ---------- STAGE 1: Build with Maven and cache ----------
FROM maven:3.8.5-openjdk-17-slim AS builder
WORKDIR /workspace

# Cache Maven dependencies
COPY pom.xml ./
RUN --mount=type=cache,target=/root/.m2 mvn dependency:go-offline -B

# Copy source and build (skip tests)
COPY src/ ./src/
RUN --mount=type=cache,target=/root/.m2 mvn clean package -DskipTests -B

# ---------- STAGE 2: Runtime image with standard JRE ----------
FROM eclipse-temurin:17-jre
WORKDIR /app

# Copy built JAR (match any jar)
COPY --from=builder /workspace/target/*.jar absm-be-deploy-0.0.1.jar

# Expose port and set active profile
EXPOSE 8080
ENV SPRING_PROFILES_ACTIVE=prod

# Start application
ENTRYPOINT ["java", "-jar", "/app/absm-be-deploy-0.0.1.jar"]
