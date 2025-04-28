# ---------- STAGE 1: Build bằng Maven ----------
FROM maven:3.8.5-openjdk-17-slim AS builder

# Thiết lập thư mục làm việc
WORKDIR /workspace

# Copy file pom và download dependencies trước để tận dụng cache
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy toàn bộ source và build (bỏ test để nhanh)
COPY src ./src
RUN mvn clean package -DskipTests -B

# ---------- STAGE 2: Runtime image ----------
FROM eclipse-temurin:17-jre

# Thư mục chứa ứng dụng
WORKDIR /app

# Copy jar đã build từ stage trước vào
COPY --from=builder /workspace/target/*.jar absm-be-deploy-0.0.1.jar

# Mặc định Spring Boot lắng nghe trên 8080
EXPOSE 8080

# Thiết lập profile nếu cần (ví dụ prod)
ENV SPRING_PROFILES_ACTIVE=prod

# Khởi động ứng dụng
ENTRYPOINT ["java","-jar","/app/absm-be-deploy-0.0.1.jar"]
