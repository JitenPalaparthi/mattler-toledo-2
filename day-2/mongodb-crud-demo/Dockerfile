# Multi-stage build
FROM maven:3.9-eclipse-temurin-22 AS build
WORKDIR /workspace
COPY app /workspace/app
RUN --mount=type=cache,target=/root/.m2 mvn -q -f app/pom.xml -DskipTests package

FROM eclipse-temurin:22-jre
WORKDIR /app
COPY --from=build /workspace/app/target/*.jar /app/app.jar
EXPOSE 8080
ENTRYPOINT ["sh","-lc","java -jar /app/app.jar"]
