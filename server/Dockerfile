#빌드 스테이지
# Gradle + JDK 21 => jar
FROM gradle:8.5-jdk21 AS build

#컨테이너 안의 작업 디텍토리 /app으로 지정
WORKDIR /app

#현재 디텍토리 전체를 컨테이너의 /app에 복사
COPY . .

#실행가능한 jar 파일 생성
RUN gradle bootJar --no-daemon

#빌드 도구없이 JDK 런타임만 있는 경량이미지로 전환
FROM eclipse-temurin:21-jdk-jammy

WORKDIR /app

#빌드한 jar파일만 가져오기
COPY --from=build /app/build/libs/*.jar app.jar

#jar파일 실행
ENTRYPOINT ["java", "-jar", "app.jar"]