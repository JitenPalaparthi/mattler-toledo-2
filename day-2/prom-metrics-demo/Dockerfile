# Multi-stage build: build with Maven, run on JRE
FROM maven:3.9-eclipse-temurin-22 AS build
WORKDIR /workspace
COPY app /workspace/app
RUN --mount=type=cache,target=/root/.m2 mvn -f app/pom.xml -q -DskipTests package

FROM eclipse-temurin:22-jre
WORKDIR /app
COPY --from=build /workspace/app/target/*.jar /app/app.jar
EXPOSE 8080
ENV JAVA_OPTS="-XX:+UseZGC -XX:MaxRAMPercentage=80"
ENTRYPOINT [ "sh", "-lc", "java $JAVA_OPTS -jar /app/app.jar" ]
