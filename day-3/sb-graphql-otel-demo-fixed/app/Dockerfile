# Build stage
FROM maven:3.9-eclipse-temurin-22 AS build
WORKDIR /workspace
COPY pom.xml .
RUN mvn -q -e -DskipTests dependency:go-offline
COPY src ./src
RUN mvn -q -DskipTests package

# Run stage
FROM eclipse-temurin:22-jre
WORKDIR /app
COPY --from=build /workspace/target/app.jar app.jar
EXPOSE 8080
# ENTRYPOINT ["java","-jar","/app/app.jar"]
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*
RUN curl -L -o /app/opentelemetry-javaagent.jar   https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/download/v2.8.0/opentelemetry-javaagent.jar
ENV JAVA_TOOL_OPTIONS="-javaagent:/app/opentelemetry-javaagent.jar"
EXPOSE 8080
ENTRYPOINT ["java","-javaagent:/app/opentelemetry-javaagent.jar","-jar","/app/app.jar"]
