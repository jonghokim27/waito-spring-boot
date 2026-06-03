FROM eclipse-temurin:21-jdk-jammy AS build

ARG MODULE
WORKDIR /workspace

COPY gradlew settings.gradle.kts build.gradle.kts ./
COPY gradle ./gradle
COPY waito-api ./waito-api
COPY waito-application ./waito-application
COPY waito-batch ./waito-batch
COPY waito-common ./waito-common
COPY waito-consumer ./waito-consumer
COPY waito-domain ./waito-domain

RUN chmod +x ./gradlew && ./gradlew ":${MODULE}:bootJar" -x test

FROM eclipse-temurin:21-jre-jammy

WORKDIR /app
ARG MODULE
RUN apt-get update && apt-get install -y --no-install-recommends curl && rm -rf /var/lib/apt/lists/*
COPY --from=build /workspace/${MODULE}/build/libs/*.jar /app/app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]