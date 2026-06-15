# 멀티스테이지 빌드
FROM gradle:8.5-jdk21 AS build
WORKDIR /app
COPY reservation-server .
RUN gradle bootJar --no-daemon

FROM eclipse-temurin:21-jdk-jammy

WORKDIR /app

COPY build/libs/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]