# 1. JDK 기반 이미지 선택
FROM eclipse-temurin:21-jdk AS build

WORKDIR /app

# 2. Gradle 캐시 최적화
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

RUN chmod +x gradlew

# 3. 소스 복사 & 빌드
COPY src src

RUN ./gradlew clean bootJar -x test

# ==============================
# 실행 단계 (최종 이미지)
# ==============================
FROM eclipse-temurin:21-jdk

WORKDIR /app

# JAR 복사
COPY --from=build /app/build/libs/*.jar app.jar

# 실행
ENTRYPOINT ["java", "-jar", "app.jar"]
