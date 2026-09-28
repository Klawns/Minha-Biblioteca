FROM maven:3.9-eclipse-temurin-21-alpine AS build

WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn -B package -DskipTests

FROM eclipse-temurin:21-jre-alpine

RUN addgroup -S biblioteca && adduser -S -G biblioteca biblioteca \
    && mkdir -p /app/data \
    && chown -R biblioteca:biblioteca /app

WORKDIR /app
COPY --from=build --chown=biblioteca:biblioteca /app/target/*.jar /app/app.jar

USER biblioteca
EXPOSE 8080
ENTRYPOINT ["java", "-Djava.awt.headless=true", "-jar", "/app/app.jar"]
