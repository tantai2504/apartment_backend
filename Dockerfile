# ---------- STAGE 1: Build bằng Maven ----------
FROM maven:3.8.5-openjdk-17-slim AS builder
WORKDIR /workspace

# copy pom & download dependencies trước để tận dụng cache
COPY pom.xml .
RUN mvn dependency:go-offline -B

# copy source và build (bỏ test để nhanh)
COPY src ./src
RUN mvn clean package -DskipTests -B

# ---------- STAGE 2: Runtime image ----------
FROM eclipse-temurin:17-jre
WORKDIR /app

# Copy jar đã build từ stage trước
COPY --from=builder /workspace/target/*.jar app.jar

# Render sẽ gán biến $PORT, Spring Boot có thể override bằng -Dserver.port
EXPOSE 8080

# Profile (nếu cần)
ENV SPRING_PROFILES_ACTIVE=prod

# Chạy app, bind vào port do Render gán (PORT) hoặc default 8080
ENTRYPOINT ["sh", "-c", \
  "java -Dserver.port=${PORT:-8080} \
        -Dspring.profiles.active=${SPRING_PROFILES_ACTIVE} \
        -jar /app/app.jar"]
