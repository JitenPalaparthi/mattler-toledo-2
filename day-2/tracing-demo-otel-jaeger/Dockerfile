# Build stage
FROM maven:3.9-eclipse-temurin-22 AS build
WORKDIR /workspace
COPY app /workspace/app
RUN --mount=type=cache,target=/root/.m2 mvn -q -f app/pom.xml -DskipTests package
# Run stage with OTel Java Agent
FROM eclipse-temurin:22-jre
WORKDIR /app
COPY --from=build /workspace/app/target/*.jar /app/app.jar
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*
RUN curl -L -o /app/opentelemetry-javaagent.jar   https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/download/v2.8.0/opentelemetry-javaagent.jar
ENV JAVA_TOOL_OPTIONS="-javaagent:/app/opentelemetry-javaagent.jar"
EXPOSE 8080
ENTRYPOINT ["java","-javaagent:/app/opentelemetry-javaagent.jar","-jar","/app/app.jar"]